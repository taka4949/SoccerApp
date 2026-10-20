
package com.example.soccerapp.data.remote.api

import com.example.soccerapp.data.remote.dto.CurrentUserDto
import com.example.soccerapp.data.remote.dto.RegisterRequestDto
import com.example.soccerapp.data.remote.dto.RegisterResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthApiService {

    @POST("auth/register")
    suspend fun register(
        @Body request: RegisterRequestDto
    ): Response<RegisterResponseDto>//HTTPステータスと本文の両方を扱えるRetrofitのResponseオブジェクト




    @GET("auth/me")
    suspend fun getCurrentUser(
        @Header("Authorization") authorization: String
    ): Response<CurrentUserDto>
}
