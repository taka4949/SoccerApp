
package com.example.soccerapp

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.soccerapp.data.repository.CommentRepository
import com.example.soccerapp.ui.state.ArchivedThreadsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch




@HiltViewModel
class ArchivedThreadsViewModel @Inject constructor(
    private val repository: CommentRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val matchId: Int =
        checkNotNull(savedStateHandle["matchId"])

    private val _uiState =
        MutableStateFlow<ArchivedThreadsUiState>(
            ArchivedThreadsUiState.Loading
        )

    val uiState: StateFlow<ArchivedThreadsUiState> =
        _uiState.asStateFlow()

    init {
        loadArchivedThreads()
    }

    fun loadArchivedThreads() {
        viewModelScope.launch {

            _uiState.value = ArchivedThreadsUiState.Loading

            try {
                val threads = repository.getArchivedThreads(
                    matchId = matchId
                )

                _uiState.value = ArchivedThreadsUiState.Success(
                    threads = threads
                )

            } catch (e: CancellationException) {
                throw e

            } catch (e: Exception) {
                _uiState.value = ArchivedThreadsUiState.Error(
                    message = e.message
                        ?: "Failed to load archived threads"
                )
            }
        }
    }
}


