package com.example.soccerapp.ui.route

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.soccerapp.MatchDetailViewModel
import com.example.soccerapp.ui.MatchThreadScreen
import com.example.soccerapp.ui.PrimaryPurple
import com.example.soccerapp.ui.PrimaryText
import com.example.soccerapp.ui.ScreenBackground
import com.example.soccerapp.ui.state.MatchDetailUiState

@Composable
fun MatchDetailRoute(matchId: Int) {
    val viewModel: MatchDetailViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(matchId) {
        viewModel.loadMatch(matchId)
    }

    when (val state = uiState) {
        MatchDetailUiState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize().background(ScreenBackground),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = PrimaryPurple)
            }
        }
        is MatchDetailUiState.Success -> {
            MatchThreadScreen(
                match = state.match,
                isRefreshing = state.isRefreshing,
                refreshError = state.refreshError,
                onRefresh = { viewModel.refreshMatch(matchId) }
            )
        }
        MatchDetailUiState.NotFound -> {
            Box(
                modifier = Modifier.fillMaxSize().background(ScreenBackground),
                contentAlignment = Alignment.Center
            ) {
                Text("試合が見つかりません", color = PrimaryText)
            }
        }
        is MatchDetailUiState.Error -> {
            Box(
                modifier = Modifier.fillMaxSize().background(ScreenBackground),
                contentAlignment = Alignment.Center
            ) {
                Text(state.message, color = PrimaryText)
            }
        }
    }
}
