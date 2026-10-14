package com.example.soccerapp.data.repository

import com.example.soccerapp.data.model.League
import com.example.soccerapp.data.model.Match
import kotlinx.coroutines.flow.Flow


interface SoccerRepository {

    suspend fun getLeagues(): List<League>

    suspend fun getMatches(
        competitionCode: String
    ): List<Match>


    suspend fun getMatchById(
        matchId: Int
    ): Match?


    suspend fun getCachedLeagues(): List<League>


    suspend fun refreshMatchById(matchId: Int): Match


    fun observeMatches(
        competitionCode: String
    ): Flow<List<Match>>

}


//SoccerRepository = 何ができるかを決める

//MatchRepository = どうやって実現するかを書く

//Hilt = interfaceと実装を結びつける

//MainViewModel =具体的な実装を知らずに interfaceだけ使う