
package com.example.soccerapp.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequestDto(//Ktorへ送る
    val email: String,
    val password: String
)
