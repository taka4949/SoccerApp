package com.example.soccerapp.data.repository

import com.example.soccerapp.data.model.Comment

interface CommentRepository {

    suspend fun getComments(//コメント取得(コメント欄開く際は、必ず①コメント取得→②保存になる）
        matchId : Int,
    ):List<Comment>


    suspend fun createComment(//保存
        matchId : Int,
        author : String,
        text : String,
    ):Comment


}