package com.example.soccerapp.server

interface CommentRepository {

    suspend fun create(
        matchId: Int,
        request: CreateCommentRequest
    ): Comment

    suspend fun getByMatchId(
        matchId: Int,
    ): List<Comment>

suspend fun getArchivedThreads(
    matchId: Int
): List<ArchivedThreadDto>


    suspend fun getArchivedComments(
        matchId: Int,
        threadId: Long
    ): List<Comment>


}