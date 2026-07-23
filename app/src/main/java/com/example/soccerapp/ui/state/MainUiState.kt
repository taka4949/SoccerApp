package com.example.soccerapp.ui.state

import com.example.soccerapp.data.model.League
import com.example.soccerapp.data.model.Match

sealed interface MainUiState {//sealedはuiの状態の種類を制限するため　　　　　　　　　　　　　　

    data object Loading : MainUiState//object→1つという意味。

    data class Success(
        val leagues: List<League>,
        val matches: List<Match>
    ) : MainUiState

    data class Error(
        val message: String
    ) : MainUiState
}