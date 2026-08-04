package com.example.soccerapp.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class MatchesResponseDto(
    val matches: List<MatchDto>//ここが最上位のデータ場。クラスのプロパティ＝中身。
)