
package com.example.soccerapp.ui.state

import com.example.soccerapp.data.model.Comment



sealed interface ArchivedCommentsUiState {

    data object Loading : ArchivedCommentsUiState

    data class Success(
        val comments: List<Comment>
    ) : ArchivedCommentsUiState

    data class Error(
        val message: String
    ) : ArchivedCommentsUiState
}


