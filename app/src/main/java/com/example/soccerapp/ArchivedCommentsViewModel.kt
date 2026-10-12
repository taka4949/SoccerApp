
package com.example.soccerapp

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.soccerapp.data.repository.CommentRepository
import com.example.soccerapp.ui.state.ArchivedCommentsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch



@HiltViewModel
class ArchivedCommentsViewModel @Inject constructor(
    private val repository: CommentRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val matchId: Int =
        checkNotNull(savedStateHandle["matchId"])

    private val threadId: Long =
        checkNotNull(savedStateHandle["threadId"])

    private val _uiState =
        MutableStateFlow<ArchivedCommentsUiState>(
            ArchivedCommentsUiState.Loading
        )

    val uiState = _uiState.asStateFlow()

    init {
        loadArchivedComments()
    }

    fun loadArchivedComments() {
        viewModelScope.launch {

            _uiState.value = ArchivedCommentsUiState.Loading

            try {
                val comments = repository.getArchivedComments(
                    matchId = matchId,
                    threadId = threadId
                )

                _uiState.value = ArchivedCommentsUiState.Success(
                    comments = comments
                )

            } catch (e: CancellationException) {
                throw e

            } catch (e: Exception) {
                _uiState.value = ArchivedCommentsUiState.Error(
                    message = e.message
                        ?: "Failed to load archived comments"
                )
            }
        }
    }
}


