package com.example.soccerapp.ui.state

import com.example.soccerapp.data.model.Comment


sealed interface MatchThreadUiState {

   data object Loading : MatchThreadUiState


    data class Success(
        val comments: List<Comment>,
        val author: String = "",
        val text: String = "",
        val isPosting: Boolean = false,//送信連打防止。
        val postErrorMessage: String? = null,//Postだけ失敗→既存コメント欄は表示させるため。
        val refreshErrorMessage: String? = null//Post→コメント1000超え→スレッド新規GETが失敗したとき利用
    ) : MatchThreadUiState

    data class Error(
        val message: String
    ) : MatchThreadUiState
}