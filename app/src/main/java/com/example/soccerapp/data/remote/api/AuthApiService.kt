
package com.example.soccerapp.data.remote.api

import com.example.soccerapp.data.remote.dto.RegisterRequestDto
import com.example.soccerapp.data.remote.dto.RegisterResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {

    @POST("auth/register")
    suspend fun register(
        @Body request: RegisterRequestDto
    ): Response<RegisterResponseDto>//HTTPステータスと本文の両方を扱えるRetrofitのResponseオブジェクト
}
