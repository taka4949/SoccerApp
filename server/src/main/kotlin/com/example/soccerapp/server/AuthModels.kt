package com.example.soccerapp.server

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(//AndroidからKtorが受け取るデータ
    val email: String,
    val password: String
)

@Serializable
data class RegisterResponse(//KtorからAndroidへ返すデータ
    val id: Long,
    val email: String
)