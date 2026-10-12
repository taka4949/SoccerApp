package com.example.soccerapp.data.remote.api

import com.example.soccerapp.data.remote.dto.ArchivedThreadDto
import com.example.soccerapp.data.remote.dto.CommentDto
import com.example.soccerapp.data.remote.dto.CreateCommentRequestDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface CommentApiService {

    @GET("matches/{matchId}/comments")
    suspend fun getComments(
        @Path("matchId") matchId: Int,
    ): List<CommentDto>

    @POST("matches/{matchId}/comments")
    suspend fun createComment(
        @Path("matchId") matchId: Int,
        @Body request: CreateCommentRequestDto,
    ): CommentDto

    @GET("matches/{matchId}/threads")
    suspend fun getArchivedThreads(
        @Path("matchId") matchId: Int,
    ): List<ArchivedThreadDto>


    @GET("matches/{matchId}/threads/{threadId}/comments")
    suspend fun getArchivedComments(
        @Path("matchId") matchId: Int,
        @Path("threadId") threadId: Long
    ): List<CommentDto>

}