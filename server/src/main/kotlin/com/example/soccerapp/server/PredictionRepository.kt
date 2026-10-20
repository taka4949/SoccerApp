package com.example.soccerapp.server

interface PredictionRepository {
    suspend fun save(userId: Long, fixture: FixtureSnapshot, home: Int, away: Int): PredictionResponse?
    suspend fun get(userId: Long, matchId: Int): PredictionResponse?
    suspend fun ranking(league: String, season: Int): List<RankingEntry>
    suspend fun recentMatchIds(): List<Int>
    suspend fun settle(matchId: Int, home: Int, away: Int)
}
