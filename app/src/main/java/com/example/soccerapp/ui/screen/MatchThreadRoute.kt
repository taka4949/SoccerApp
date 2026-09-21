package com.example.soccerapp.ui.screen

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.soccerapp.MatchThreadViewModel
import com.example.soccerapp.ui.CommentSection
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.soccerapp.ui.CommentPostSection
import com.example.soccerapp.ui.state.MatchThreadUiState


@Composable
fun ColumnScope.MatchThreadRoute(//親columnのスコープ→この中でweight使える（深堀？）
    matchId: Int
) {
    val viewModel: MatchThreadViewModel = hiltViewModel()


    val uiState by viewModel.uiState.collectAsStateWithLifecycle()


    val isPosting = (uiState as? MatchThreadUiState.Success)?.isPosting ?: false
    //↑これの別の書き方→smart.castできない→val uiStateは固定ではない→一度確認しても二度目は保証できない、とざっくり理解で留めておく。
    //Success→isPosting取得、それ以外→is~が存在しないため、false



    LaunchedEffect(matchId) {//initとの違い。こちらはComposable内で動く→変化するデータを扱う点でinitと違う。
        viewModel.loadComments(matchId)//get関数
    }

    CommentSection(
        matchId = matchId,
        uiState = uiState,
        onRetry = viewModel::loadComments,
        modifier = Modifier.weight(1f)//columnの余分な部分を使える
    )

    CommentPostSection(
        isPosting = isPosting,
        onPostComment = { author, text ->
            viewModel.postComment(
                matchId = matchId,
                author = author,
                text = text
            )
        }
    )
}





//owner = ViewModelを誰に所属させて、いつまで保持するか（深堀必須、将来的）
//
//Factory = ViewModelをどうやって生成するか
//Activity内→hilt対応,BackstackEntry内→デフォルトになる。hiltViewModelと明示する必要がある！。