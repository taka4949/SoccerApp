
package com.example.soccerapp.ui.state

import com.example.soccerapp.data.model.Prediction

sealed interface PredictionUiState {



    data object Loading : PredictionUiState





    data class Success(
        val prediction: Prediction? = null,
        val homeScoreInput: String = "",
        val awayScoreInput: String = "",
        val isSubmitting: Boolean = false,
        val submitErrorMessage: String? = null
    ) : PredictionUiState





    data class Error(
        val message: String
    ) : PredictionUiState
}
