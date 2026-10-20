package com.example.soccerapp.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponseDto(
    val accessToken: String,
    val expiresAt: String,
    val userId: Long
)
