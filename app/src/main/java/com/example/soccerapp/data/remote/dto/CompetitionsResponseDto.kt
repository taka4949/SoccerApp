package com.example.soccerapp.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class CompetitionsResponseDto(
    val competitions: List<CompetitionDto>//リストの理由は、データを複数取得するため。
)