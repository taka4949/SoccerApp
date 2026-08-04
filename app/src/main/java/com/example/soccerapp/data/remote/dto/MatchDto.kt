package com.example.soccerapp.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class MatchDto(
    val id: Int,
    val competition: CompetitionDto,
    val utcDate: String,
    val status: String,
    val homeTeam: TeamDto,
    val awayTeam: TeamDto,
    val score: ScoreDto
)