package com.example.soccerapp.data.repository

import com.example.soccerapp.data.model.League
import com.example.soccerapp.data.model.Match


interface SoccerRepository {

    suspend fun getLeagues(): List<League>

    suspend fun getMatches(): List<Match>
}