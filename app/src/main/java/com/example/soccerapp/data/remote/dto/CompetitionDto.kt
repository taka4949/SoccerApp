package com.example.soccerapp.data.remote.dto

import kotlinx.serialization.Serializable//json変換コードを生成する

@Serializable
data class CompetitionDto(//変換後の値を入れる。
    val id : Int,
    val name : String,
    val code : String
)