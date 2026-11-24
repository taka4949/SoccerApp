
package com.example.soccerapp.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class PredictionDto(
    val id: Long,
    val matchId: Int,
    val predictedHomeScore: Int,
    val predictedAwayScore: Int,
    val earnedPoints: Int? = null
)
