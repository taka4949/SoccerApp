package com.example.soccerapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "matches")
data class MatchEntity(
    @PrimaryKey//行の識別を可能にする
    val id: Int,
    val leagueId: String,
    val homeTeam: String,
    val awayTeam: String,
    val homeScore: Int?,
    val awayScore: Int?,
    val utcDate: String,
    val status: String
)


//@Entity:Roomに「これは保存対象のクラスです」と伝える

//tableName = "matches":データベース内のテーブル名をmatchesにする

//MatchEntity:1試合分の保存データ

//各プロパティ:テーブルの各列になる