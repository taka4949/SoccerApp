package com.example.soccerapp.ui.screen

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.soccerapp.MatchDetailViewModel
import com.example.soccerapp.ui.MatchThreadScreen
import com.example.soccerapp.ui.state.MatchDetailUiState


@Composable
fun MatchDetailRoute(
    matchId: Int
) {
    val viewModel: MatchDetailViewModel = hiltViewModel()

    val uiState by
    viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(matchId) {
        viewModel.loadMatch(matchId)
    }

    when (val state = uiState) {
        MatchDetailUiState.Loading -> {
            CircularProgressIndicator()
        }

        is MatchDetailUiState.Success -> {
            MatchThreadScreen(
                match = state.match
            )
        }

        MatchDetailUiState.NotFound -> {
            Text("Match not found")
        }

        is MatchDetailUiState.Error -> {
            Text(state.message)
        }
    }
}