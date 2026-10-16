
package com.example.soccerapp.ui.state

sealed interface RegisterUiState {

    data object Idle : RegisterUiState//入力フォーム表示

    data object Loading : RegisterUiState//処理中

    data class Success(
        val id: Long,
        val email: String
    ) : RegisterUiState

    data class Error(
        val message: String
    ) : RegisterUiState
}
