
package com.example.soccerapp.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.soccerapp.ArchivedCommentsViewModel
import com.example.soccerapp.ui.ArchivedCommentsScreen



@Composable
fun ArchivedCommentsRoute() {

    val viewModel: ArchivedCommentsViewModel = hiltViewModel()

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ArchivedCommentsScreen(
        uiState = uiState,
        onRetry = viewModel::loadArchivedComments
    )
}




