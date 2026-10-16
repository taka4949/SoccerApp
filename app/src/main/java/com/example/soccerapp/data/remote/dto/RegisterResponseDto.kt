
package com.example.soccerapp.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class RegisterResponseDto(//Ktorから貰う
    val id: Long,
    val email: String
)
