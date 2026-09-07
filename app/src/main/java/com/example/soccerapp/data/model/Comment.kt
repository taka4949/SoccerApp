package com.example.soccerapp.data.model

data class Comment(
    val id: Long,
    val matchId: Int,
    val author: String,
    val text: String,
    val createdAt: String,
)