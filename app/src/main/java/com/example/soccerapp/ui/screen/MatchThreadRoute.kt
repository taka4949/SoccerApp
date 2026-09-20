package com.example.soccerapp.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.soccerapp.MatchThreadViewModel
import com.example.soccerapp.ui.CommentSection
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
fun MatchThreadRoute(
    matchId: Int
) {
    val viewModel: MatchThreadViewModel = hiltViewModel()


    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(matchId) {//initとの違い。こちらはComposable内で動く→変化するデータを扱う点でinitと違う。
        viewModel.loadComments(matchId)
    }

    CommentSection(
        matchId = matchId,
        uiState = uiState,
        onRetry = viewModel::loadComments
    )
}

//owner = ViewModelを誰に所属させて、いつまで保持するか（深堀必須、将来的）
//
//Factory = ViewModelをどうやって生成するか
//Activity内→hilt対応,BackstackEntry内→デフォルトになる。hiltViewModelと明示する必要がある！。