package com.example.soccerapp.data.repository


import com.example.soccerapp.data.model.Comment
import com.example.soccerapp.data.remote.api.CommentApiService
import com.example.soccerapp.data.remote.dto.CreateCommentRequestDto
import com.example.soccerapp.data.repository.CommentRepository
import javax.inject.Inject

class NetworkCommentRepository @Inject constructor(
    private val commentApiService: CommentApiService,
) : CommentRepository {

    override suspend fun createComment(
        matchId: Int,
        author: String,
        text: String,
    ): Comment {
        val commentDto = commentApiService.createComment(
            matchId = matchId,
            request = CreateCommentRequestDto(
                author = author,
                text = text,
            ),
        )

        return Comment(
            id = commentDto.id,
            matchId = commentDto.matchId,
            author = commentDto.author,
            text = commentDto.text,
            createdAt = commentDto.createdAt,
        )
    }

    override suspend fun getComments(matchId: Int): List<Comment> {

        val commentDtos = commentApiService.getComments(matchId)


        return commentDtos.map{commentDto ->
            Comment(
                id = commentDto.id,
                matchId = commentDto.matchId,
                author = commentDto.author,
                text = commentDto.text,
                createdAt = commentDto.createdAt,

            )
        }
    }

}