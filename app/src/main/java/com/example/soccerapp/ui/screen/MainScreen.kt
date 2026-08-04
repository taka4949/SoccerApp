package com.example.soccerapp.ui.screen

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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.soccerapp.MainViewModel
import com.example.soccerapp.navigation.AppNavigation
import com.example.soccerapp.ui.state.MainUiState


@Composable
fun MainRoute(
    mainViewModel: MainViewModel = viewModel()//ここでインスタンス取得。init開始。


) {
    val uiState by
    mainViewModel.uiState.collectAsStateWithLifecycle()
    //ここで、変数の変化を観測し、↓へ再コンポーズ促す。

    MainScreen(
        uiState = uiState,
        onRetry = mainViewModel::loadData
    )
}

@Composable
fun MainScreen(
    uiState: MainUiState,
    onRetry: () -> Unit//関数の引数なし、戻り値なし
) {
    when (uiState) {
        MainUiState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        is MainUiState.Success -> {//ここで初めて画面に表示される。リーグなど。
            AppNavigation(
                leagues = uiState.leagues,
                matches = uiState.matches
            )
        }

        is MainUiState.Error -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = uiState.message)

                Button(onClick = onRetry) {
                    Text(text = "Retry")
                }
            }
        }
    }
}