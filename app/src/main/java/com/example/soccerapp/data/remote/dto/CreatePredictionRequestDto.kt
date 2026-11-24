
package com.example.soccerapp.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class CreatePredictionRequestDto(
    val predictedHomeScore: Int,
    val predictedAwayScore: Int
)
