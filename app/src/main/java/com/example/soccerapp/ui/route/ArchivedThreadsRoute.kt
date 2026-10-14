
package com.example.soccerapp.ui.route

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.soccerapp.ArchivedThreadsViewModel
import com.example.soccerapp.ui.ArchivedThreadsScreen

@Composable
fun ArchivedThreadsRoute(
    onThreadClick: (Long) -> Unit
) {
    val viewModel: ArchivedThreadsViewModel = hiltViewModel()

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ArchivedThreadsScreen(
        uiState = uiState,
        onThreadClick = onThreadClick,
        onRetry = viewModel::loadArchivedThreads
    )
}
