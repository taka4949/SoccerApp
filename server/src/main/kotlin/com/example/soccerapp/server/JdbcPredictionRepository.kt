package com.example.soccerapp.server

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.sql.Timestamp

class JdbcPredictionRepository : PredictionRepository {
    override suspend fun save(
        userId: Long,
        fixture: FixtureSnapshot,
        home: Int,
        away: Int
    ): PredictionResponse? = withContext(Dispatchers.IO) {
        DatabaseFactory.getConnection().use { connection ->
            connection.prepareStatement(
                """
                INSERT INTO predictions (
                    user_id, match_id, league_code, season_year,
                    home_score, away_score, kickoff_at
                )
                SELECT ?, ?, ?, ?, ?, ?, ?
                WHERE ? > CURRENT_TIMESTAMP
                ON CONFLICT (user_id, match_id)
                DO UPDATE SET home_score = EXCLUDED.home_score,
                              away_score = EXCLUDED.away_score,
                              submitted_at = CURRENT_TIMESTAMP
                WHERE predictions.kickoff_at > CURRENT_TIMESTAMP
                  AND EXCLUDED.kickoff_at > CURRENT_TIMESTAMP
                RETURNING match_id, home_score, away_score, submitted_at, points
                """.trimIndent()
            ).use { statement ->
                statement.setLong(1, userId)
                statement.setInt(2, fixture.matchId)
                statement.setString(3, fixture.leagueCode)
                statement.setInt(4, fixture.seasonYear)
                statement.setInt(5, home)
                statement.setInt(6, away)
                statement.setTimestamp(7, Timestamp.from(fixture.kickoffAt))
                statement.setTimestamp(8, Timestamp.from(fixture.kickoffAt))
                statement.executeQuery().use { result ->
                    if (result.next()) PredictionResponse(
                        matchId = result.getInt("match_id"),
                        homeScore = result.getInt("home_score"),
                        awayScore = result.getInt("away_score"),
                        predictedAt = result.getTimestamp("submitted_at").toInstant().toString(),
                        points = result.getInt("points").takeUnless { result.wasNull() }
                    ) else null
                }
            }
        }
    }

    override suspend fun get(userId: Long, matchId: Int): PredictionResponse? =
        withContext(Dispatchers.IO) {
            DatabaseFactory.getConnection().use { connection ->
                connection.prepareStatement(
                    "SELECT match_id, home_score, away_score, submitted_at, points FROM predictions WHERE user_id = ? AND match_id = ?"
                ).use { statement ->
                    statement.setLong(1, userId)
                    statement.setInt(2, matchId)
                    statement.executeQuery().use { result ->
                        if (result.next()) PredictionResponse(
                            matchId = result.getInt("match_id"),
                            homeScore = result.getInt("home_score"),
                            awayScore = result.getInt("away_score"),
                            predictedAt = result.getTimestamp("submitted_at").toInstant().toString(),
                            points = result.getInt("points").takeUnless { result.wasNull() }
                        ) else null
                    }
                }
            }
        }

    override suspend fun ranking(league: String, season: Int): List<RankingEntry> =
        withContext(Dispatchers.IO) {
            DatabaseFactory.getConnection().use { connection ->
                connection.prepareStatement(
                    """
                    WITH totals AS (
                        SELECT user_id, SUM(COALESCE(points, 0)) AS total
                        FROM predictions
                        WHERE league_code = ? AND season_year = ?
                        GROUP BY user_id
                    )
                    SELECT user_id, total,
                           DENSE_RANK() OVER (ORDER BY total DESC) AS position
                    FROM totals
                    ORDER BY total DESC, user_id
                    LIMIT 100
                    """.trimIndent()
                ).use { statement ->
                    statement.setString(1, league)
                    statement.setInt(2, season)
                    statement.executeQuery().use { result ->
                        buildList {
                            while (result.next()) {
                                val id = result.getLong("user_id")
                                add(
                                    RankingEntry(
                                        rank = result.getInt("position"),
                                        userId = id,
                                        displayName = "Player #$id",
                                        points = result.getLong("total")
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

    override suspend fun recentMatchIds(): List<Int> = withContext(Dispatchers.IO) {
        DatabaseFactory.getConnection().use { connection ->
            connection.prepareStatement(
                """
                SELECT match_id FROM predictions
                WHERE kickoff_at < CURRENT_TIMESTAMP
                  AND kickoff_at > CURRENT_TIMESTAMP - INTERVAL '7 days'
                GROUP BY match_id
                ORDER BY MAX(kickoff_at) DESC
                LIMIT 8
                """.trimIndent()
            ).use { statement ->
                statement.executeQuery().use { result ->
                    buildList { while (result.next()) add(result.getInt(1)) }
                }
            }
        }
    }

    override suspend fun settle(matchId: Int, home: Int, away: Int) {
        withContext(Dispatchers.IO) {
            DatabaseFactory.getConnection().use { connection ->
                connection.autoCommit = false
                try {
                    val scores = connection.prepareStatement(
                        "SELECT user_id, home_score, away_score FROM predictions WHERE match_id = ? FOR UPDATE"
                    ).use { statement ->
                        statement.setInt(1, matchId)
                        statement.executeQuery().use { result ->
                            buildList {
                                while (result.next()) add(
                                    result.getLong("user_id") to predictionPoints(
                                        result.getInt("home_score"),
                                        result.getInt("away_score"),
                                        home, away
                                    )
                                )
                            }
                        }
                    }
                    connection.prepareStatement(
                        "UPDATE predictions SET points = ?, scored_at = CURRENT_TIMESTAMP WHERE user_id = ? AND match_id = ?"
                    ).use { statement ->
                        for ((userId, points) in scores) {
                            statement.setInt(1, points)
                            statement.setLong(2, userId)
                            statement.setInt(3, matchId)
                            statement.addBatch()
                        }
                        statement.executeBatch()
                    }
                    connection.commit()
                } catch (e: Exception) {
                    connection.rollback()
                    throw e
                }
            }
        }
    }
}
