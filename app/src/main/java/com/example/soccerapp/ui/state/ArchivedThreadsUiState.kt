
package com.example.soccerapp.ui.state

import com.example.soccerapp.data.model.ArchivedThread

sealed interface ArchivedThreadsUiState {

    data object Loading : ArchivedThreadsUiState

    data class Success(
        val threads: List<ArchivedThread>
    ) : ArchivedThreadsUiState

    data class Error(
        val message: String
    ) : ArchivedThreadsUiState
}
