package com.example.soccerapp.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.soccerapp.data.local.entity.LeagueEntity


@Dao
interface LeagueDao {


@Upsert
suspend fun upsertLeagues(
    leagues: List<LeagueEntity>
)

@Query("SELECT * FROM leagues")
suspend fun getLeagues(): List<LeagueEntity>


}