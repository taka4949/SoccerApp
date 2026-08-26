package com.example.soccerapp.server

import kotlinx.serialization.Serializable

@Serializable//jsonからkotlinのデータクラスへ変換するっていう設定
data class CreateCommentRequest(//androidからktorが受け取る未完成のデータ
    val author: String,
    val text: String,
)

@Serializable
data class Comment(//ktorが作って保存、androidへ返却する完成データ
    val id: Long,//サーバー側が決める必要があるから。
    val matchId: Int,//同様。
    val author: String,
    val text: String,
    val createdAt: String,
)