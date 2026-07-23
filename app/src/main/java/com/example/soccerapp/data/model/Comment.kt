package com.example.soccerapp.data.model

data class Comment(
    val id : Int,
    val matchId : Int,
    val text : String,
    val createdAt : String
)
