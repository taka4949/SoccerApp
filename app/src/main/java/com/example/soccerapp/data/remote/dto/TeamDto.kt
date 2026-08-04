package com.example.soccerapp.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class TeamDto(
    val id: Int?,
    val name: String?
)