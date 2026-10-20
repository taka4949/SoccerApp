package com.example.soccerapp.server

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(val email: String, val password: String)

@Serializable
data class SessionResponse(
    val accessToken: String,
    val expiresAt: String,
    val userId: Long
)

@Serializable
data class AccountResponse(val userId: Long)
