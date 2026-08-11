package com.example.soccerapp.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.soccerapp.data.local.entity.MatchEntity

@Dao//どう保存するのかを決める
interface MatchDao {

    @Upsert//新しいものは追加し、既存の試合は他を更新する。ここで保存する。
    suspend fun upsertMatches(matches: List<MatchEntity>)

    @Query("SELECT * FROM matches WHERE leagueId = :leagueId")//selectで前列取得、合致するIDの試合を取得という条件
    suspend fun getMatchesByLeague(
        leagueId: String
    ): List<MatchEntity>
}