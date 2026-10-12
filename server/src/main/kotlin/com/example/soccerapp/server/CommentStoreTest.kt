package com.example.soccerapp.server

import java.time.Instant

class CommentStoreTest : CommentRepository {//テスト用
    private val comments = mutableListOf<Comment>()
    private var nextId = 1L

   override suspend fun create(
        matchId: Int,
        request: CreateCommentRequest,//データクラス
    ): Comment {
        val comment = Comment(
            id = nextId,
            threadId = 1L,
            matchId = matchId,
            author = request.author,
            text = request.text,
            createdAt = Instant.now().toString(),
        )

        comments.add(comment)//一応ここで保存している。
        nextId += 1

        return comment
    }

    override suspend fun getByMatchId(matchId: Int): List<Comment> {//掲示板を開くときに動く
        return comments.filter { comment ->//今の段階では、リストにごちゃまぜでコメントが入っている。
            comment.matchId == matchId
        }
    }



    override suspend fun getArchivedThreads(
        matchId: Int
    ): List<ArchivedThreadDto> = emptyList()




    override suspend fun getArchivedComments(
        matchId: Int,
        threadId: Long
    ): List<Comment> {
        return comments.filter { comment ->
            comment.matchId == matchId &&
                    comment.threadId == threadId
        }
    }
}
//jdbcCommentRepositoryが本番用。
