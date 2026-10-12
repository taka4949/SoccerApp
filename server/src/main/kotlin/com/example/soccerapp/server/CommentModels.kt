package com.example.soccerapp.server

import kotlinx.serialization.Serializable

@Serializable//jsonからkotlinのデータクラスへ変換するっていう設定
data class CreateCommentRequest(//androidからKtorが受け取るデータ
    val author: String,
    val text: String,
)

@Serializable
data class Comment(//androidへ返すデータ
    val id: Long,//サーバー側が決める必要がある。
    val matchId: Int,//同様。
    val author: String,
    val text: String,
    val createdAt: String,
    val threadId: Long,
)

