package com.example.soccerapp.data.model

data class Match(
    val id: Int,
    val leagueId: String,
    val homeTeam: String,
    val awayTeam: String,
    val homeScore: Int,
    val awayScore: Int,
    val matchTime: Int
)
