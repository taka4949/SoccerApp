package com.example.soccerapp.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class FullTimeScoreDto(
    val home: Int?,
    val away: Int?
)