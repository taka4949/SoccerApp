package com.example.soccerapp.ui.state

import com.example.soccerapp.data.model.Match

sealed interface MatchDetailUiState {

    data object Loading : MatchDetailUiState

    data class Success(
        val match: Match,
        val isRefreshing: Boolean = false,
        val refreshError: String? = null
    ) : MatchDetailUiState

    data object NotFound : MatchDetailUiState

    data class Error(
        val message: String
    ) : MatchDetailUiState
}