package com.example.soccerapp.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class CreateCommentRequestDto(
    val author: String,
    val text: String,
)//AndroidがKtorへ送るJSON