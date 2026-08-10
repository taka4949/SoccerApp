package com.example.soccerapp.data.model

data class Match(//dtoからくるデータをui用として受け取る。dtoから直接←これは依存関係を作ってしまう。(後々深く理解）
    val id: Int,
    val leagueId: String,
    val homeTeam: String,
    val awayTeam: String,
    val homeScore: Int?,//null許容。試合前は0ではなくnull。
    val awayScore: Int?,
    val utcDate: String,
    val status: String
)