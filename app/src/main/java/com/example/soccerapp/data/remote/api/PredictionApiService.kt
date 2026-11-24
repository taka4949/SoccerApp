
package com.example.soccerapp.data.remote.api

import com.example.soccerapp.data.remote.dto.CreatePredictionRequestDto
import com.example.soccerapp.data.remote.dto.PredictionDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface PredictionApiService {

    @POST("matches/{matchId}/predictions")
    suspend fun createPrediction(
        @Path("matchId") matchId: Int,
        @Body request: CreatePredictionRequestDto
    ): PredictionDto





    @GET("matches/{matchId}/predictions/me")
    suspend fun getMyPrediction(
        @Path("matchId") matchId: Int
    ): Response<PredictionDto>
}


//Responseは、、サーバーから返ってきたHTTPレスポンス全体を扱うためのRetrofitの型