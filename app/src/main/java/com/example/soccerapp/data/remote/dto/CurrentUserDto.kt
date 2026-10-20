package com.example.soccerapp.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class CurrentUserDto(//トークン認証時に利用
    val userId: Long
)