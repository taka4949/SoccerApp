package com.example.soccerapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.soccerapp.data.repository.SoccerRepository
import com.example.soccerapp.ui.state.MatchDetailUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch



@HiltViewModel
class MatchDetailViewModel @Inject constructor(
    private val repository: SoccerRepository
) : ViewModel() {

    private val _uiState =
        MutableStateFlow<MatchDetailUiState>(
            MatchDetailUiState.Loading
        )

    val uiState = _uiState.asStateFlow()

    fun loadMatch(matchId: Int) {
        viewModelScope.launch {

            _uiState.value = MatchDetailUiState.Loading

            try {
                val match =
                    repository.getMatchById(matchId)

                _uiState.value =
                    if (match == null) {//取得するものが1件だから、null許容。
                        MatchDetailUiState.NotFound
                    } else {
                        MatchDetailUiState.Success(
                            match = match
                        )
                    }

            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.value =
                    MatchDetailUiState.Error(
                        message =
                            e.message
                                ?: "Failed to load match"
                    )
            }
        }
    }
}