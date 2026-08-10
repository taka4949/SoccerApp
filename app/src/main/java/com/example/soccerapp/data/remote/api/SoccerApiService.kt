package com.example.soccerapp.data.remote.api
import com.example.soccerapp.data.remote.dto.MatchesResponseDto
import retrofit2.http.Path
import retrofit2.http.Query



import com.example.soccerapp.data.remote.dto.CompetitionsResponseDto
import retrofit2.http.GET

interface SoccerApiService {

    @GET("competitions")
    suspend fun getCompetitions(): CompetitionsResponseDto//関数名と戻り値の型、リーグ一覧を取得
    //ここでresponseDtoはnetworkmodule.ktでprovideretrofit内からくる。



    @GET("competitions/{competitionCode}/matches")
    suspend fun getMatches(
        @Path("competitionCode") competitionCode: String,//urlの途中へ引数の値を差し替える。
        @Query("status") status: String//条件
    ): MatchesResponseDto
}