package com.example.soccerapp.data.remote.api
import com.example.soccerapp.data.remote.dto.MatchesResponseDto
import retrofit2.http.Path
import retrofit2.http.Query



import com.example.soccerapp.data.remote.dto.CompetitionsResponseDto
import com.example.soccerapp.data.remote.dto.MatchDto
import retrofit2.http.GET

interface SoccerApiService {

    @GET("competitions")
    suspend fun getCompetitions(): CompetitionsResponseDto//関数名と戻り値の型、リーグ一覧を取得
    //ここでresponseDtoはNetWorkModule.kt内のProvideRetrofit関数内からくる。
    //実装がない関数はreturnをかけないため。



    @GET("competitions/{competitionCode}/matches")
    suspend fun getMatches(
        @Path("competitionCode") competitionCode: String,//urlの途中へ引数の値を差し替える。
    ): MatchesResponseDto


    @GET("matches/{matchId}")
    suspend fun getMatch(
        @Path("matchId") matchId: Int
    ): MatchDto
}