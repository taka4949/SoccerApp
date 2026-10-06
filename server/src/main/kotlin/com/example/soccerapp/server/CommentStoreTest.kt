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
}
//jdbcCommentRepositoryが本番用。
