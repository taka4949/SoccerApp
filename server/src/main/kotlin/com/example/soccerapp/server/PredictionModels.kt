package com.example.soccerapp.server

import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
data class SavePredictionRequest(val homeScore: Int, val awayScore: Int)

@Serializable
data class PredictionResponse(
    val matchId: Int,
    val homeScore: Int,
    val awayScore: Int,
    val predictedAt: String,
    val points: Int?
)

@Serializable
data class RankingEntry(
    val rank: Int,
    val userId: Long,
    val displayName: String,
    val points: Long
)

data class FixtureSnapshot(
    val matchId: Int,
    val leagueCode: String,
    val seasonYear: Int,
    val kickoffAt: Instant,
    val status: String,
    val homeScore: Int?,
    val awayScore: Int?
)

fun predictionPoints(home: Int, away: Int, resultHome: Int, resultAway: Int): Int =
    when {
        home == resultHome && away == resultAway -> 3
        home.compareTo(away) == resultHome.compareTo(resultAway) -> 1
        else -> 0
}
