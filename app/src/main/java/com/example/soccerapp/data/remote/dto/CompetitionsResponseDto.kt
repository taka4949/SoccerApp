package com.example.soccerapp.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class CompetitionsResponseDto(//データクラスは変数をまとめて持つ、下の変数もつにはデータクラス.competionへ
    val competitions: List<CompetitionDto>//リストの理由は、データを複数取得するため。
)