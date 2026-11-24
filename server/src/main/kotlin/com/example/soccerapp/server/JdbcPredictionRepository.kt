
package com.example.soccerapp.server

import java.sql.ResultSet
import java.sql.SQLException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface PredictionRepository {

    suspend fun create(
        userId: Long,
        matchId: Int,
        request: CreatePredictionRequest
    ): PredictionResponse

    suspend fun getMyPrediction(
        userId: Long,
        matchId: Int
    ): PredictionResponse?
}






class PredictionAlreadyExistsException(
    cause: SQLException
) : RuntimeException("Prediction already exists", cause)

class JdbcPredictionRepository : PredictionRepository {

    override suspend fun create(
        userId: Long,
        matchId: Int,
        request: CreatePredictionRequest
    ): PredictionResponse = withContext(Dispatchers.IO) {

        try {
            DatabaseFactory.getConnection().use { connection ->

                connection.prepareStatement(
                    """
                    INSERT INTO predictions (
                        user_id,
                        match_id,
                        predicted_home_score,
                        predicted_away_score
                    )
                    VALUES (?, ?, ?, ?)
                    RETURNING
                        id,
                        match_id,
                        predicted_home_score,
                        predicted_away_score,
                        earned_points
                    """.trimIndent()
                ).use { statement ->

                    statement.setLong(1, userId)
                    statement.setInt(2, matchId)
                    statement.setInt(3, request.predictedHomeScore)
                    statement.setInt(4, request.predictedAwayScore)

                    statement.executeQuery().use { result ->
                        check(result.next()) {
                            "Created prediction was not returned"
                        }

                        result.toPredictionResponse()
                    }
                }
            }
        } catch (e: SQLException) {//重複予想を防ぐ
            if (e.sqlState == "23505") {
                throw PredictionAlreadyExistsException(e)
            }
            throw e
        }
    }

    override suspend fun getMyPrediction(
        userId: Long,
        matchId: Int
    ): PredictionResponse? = withContext(Dispatchers.IO) {

        DatabaseFactory.getConnection().use { connection ->

            connection.prepareStatement(
                """
                SELECT
                    id,
                    match_id,
                    predicted_home_score,
                    predicted_away_score,
                    earned_points
                FROM predictions
                WHERE user_id = ?
                  AND match_id = ?
                """.trimIndent()
            ).use { statement ->

                statement.setLong(1, userId)
                statement.setInt(2, matchId)

                statement.executeQuery().use { result ->
                    if (result.next()) {
                        result.toPredictionResponse()
                    } else {
                        null
                    }
                }
            }
        }
    }
}

private fun ResultSet.toPredictionResponse(): PredictionResponse {
    val points = getInt("earned_points")
    val earnedPoints = if (wasNull()) null else points

    return PredictionResponse(
        id = getLong("id"),
        matchId = getInt("match_id"),
        predictedHomeScore = getInt("predicted_home_score"),
        predictedAwayScore = getInt("predicted_away_score"),
        earnedPoints = earnedPoints
    )
}
