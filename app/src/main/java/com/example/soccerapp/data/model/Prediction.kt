package com.example.soccerapp.data.model

data class Prediction(
    val id: Long,
    val matchId: Int,
    val predictedHomeScore: Int,
    val predictedAwayScore: Int,
    val earnedPoints: Int?
)
