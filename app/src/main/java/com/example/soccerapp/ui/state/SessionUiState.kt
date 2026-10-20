package com.example.soccerapp.ui.state

sealed interface SessionUiState {
    data object Loading : SessionUiState

    data class LoggedIn(
        val userId: Long
    ) : SessionUiState

    data object LoggedOut : SessionUiState

    data object Unavailable : SessionUiState
}