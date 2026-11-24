
package com.example.soccerapp

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.soccerapp.data.repository.PredictionRepository
import com.example.soccerapp.ui.state.PredictionUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class PredictionViewModel @Inject constructor(
    private val repository: PredictionRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val matchId: Int =
        checkNotNull(savedStateHandle["matchId"])

    private val _uiState = MutableStateFlow<PredictionUiState>(
        PredictionUiState.Loading
    )

    val uiState = _uiState.asStateFlow()

    init {
        loadPrediction()
    }






    fun loadPrediction() {
        val currentState = _uiState.value

        if (currentState is PredictionUiState.Success) return

        _uiState.value = PredictionUiState.Loading

        viewModelScope.launch {
            try {
                val prediction = repository.getMyPrediction(matchId)//予想の確認

                _uiState.value = PredictionUiState.Success(
                    prediction = prediction,
                    homeScoreInput = prediction
                        ?.predictedHomeScore
                        ?.toString()
                        .orEmpty(),
                    awayScoreInput = prediction
                        ?.predictedAwayScore
                        ?.toString()
                        .orEmpty()
                )




            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.value = PredictionUiState.Error(
                    message = "Failed to load prediction"
                )
            }
        }
    }





    fun onHomeScoreChange(value: String) {
        val currentState =
            _uiState.value as? PredictionUiState.Success
                ?: return

        if (currentState.isSubmitting ||
            currentState.prediction != null
        ) return

//送信中の投稿または、一度予想を登録している場合は、再投稿防止。（いったん）


        _uiState.value = currentState.copy(
            homeScoreInput = value,
            submitErrorMessage = null
        )
    }

    //ユーザーが「2」を入力
    //        ↓
    //OutlinedTextField.onValueChange("2")
    //        ↓
    //viewModel.onHomeScoreChange("2")
    //        ↓
    //現在のUiStateを取得
    //        ↓
    //currentState.copy(
    //    homeScoreInput = "2"
    //)
    //        ↓
    //_uiState.value を更新
    //        ↓
    //Composeが新しいUiStateを受け取る
    //        ↓
    //入力欄に「2」が表示される






    fun onAwayScoreChange(value: String) {
        val currentState =
            _uiState.value as? PredictionUiState.Success
                ?: return

        if (currentState.isSubmitting ||
            currentState.prediction != null
        ) return

        _uiState.value = currentState.copy(
            awayScoreInput = value,
            submitErrorMessage = null
        )
    }





    fun submitPrediction() {//load関数後でなければ、動かない。
        val currentState =
            _uiState.value as? PredictionUiState.Success
                ?: return

        if (currentState.isSubmitting ||
            currentState.prediction != null
        ) return



        val homeScore = currentState.homeScoreInput.toIntOrNull()
        val awayScore = currentState.awayScoreInput.toIntOrNull()




        if (homeScore == null || awayScore == null ||
            homeScore < 0 || awayScore < 0
        ) {
            _uiState.value = currentState.copy(
                submitErrorMessage = "Enter valid scores (0 or greater)"
            )
            return
        }





        _uiState.value = currentState.copy(
            isSubmitting = true,
            submitErrorMessage = null
        )




        viewModelScope.launch {
            try {
                val prediction = repository.createPrediction(//予想の保存
                    matchId = matchId,
                    predictedHomeScore = homeScore,
                    predictedAwayScore = awayScore
                )

                _uiState.value = currentState.copy(
                    prediction = prediction,
                    isSubmitting = false,
                    submitErrorMessage = null
                )

            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.value = currentState.copy(
                    isSubmitting = false,
                    submitErrorMessage = "Failed to submit prediction"
                )
            }
        }
    }
}
