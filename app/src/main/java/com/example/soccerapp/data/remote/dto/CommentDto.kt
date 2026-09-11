package com.example.soccerapp.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class CommentDto(
    val id: Long,
    val matchId: Int,
    val author: String,
    val text: String,
    val createdAt: String,
)//KtorからAndroidヘ帰ってきたデータのDTO