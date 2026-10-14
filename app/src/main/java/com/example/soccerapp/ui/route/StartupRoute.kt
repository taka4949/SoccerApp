package com.example.soccerapp.ui.route

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.soccerapp.StartupViewModel


@Composable
fun StartupRoute(
    viewModel: StartupViewModel = hiltViewModel()
) {
    val showPrompt by
    viewModel.showAccountPrompt.collectAsStateWithLifecycle()

    when (showPrompt) {
        null -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        true -> {
            AccountPromptScreen(
                onSkip = viewModel::dismissAccountPrompt
            )
        }

        false -> {
            MainRoute()
        }
    }
}

@Composable
private fun AccountPromptScreen(
    onSkip: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "アカウント登録のご案内")

        Button(onClick = onSkip) {
            Text(text = "スキップして始める")
        }
    }
}