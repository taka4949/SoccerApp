package com.example.soccerapp.data.repository

import com.example.soccerapp.data.model.League
import com.example.soccerapp.data.model.Match


interface SoccerRepository {

    suspend fun getLeagues(): List<League>

    suspend fun getMatches(
        competitionCode: String
    ): List<Match>

}


//SoccerRepository = 何ができるかを決める

//MatchRepository = どうやって実現するかを書く

//Hilt = interfaceと実装を結びつける

//MainViewModel =具体的な実装を知らずに interfaceだけ使う