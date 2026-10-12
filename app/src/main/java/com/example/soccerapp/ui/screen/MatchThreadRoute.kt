package com.example.soccerapp.ui.screen

import android.util.Log
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
import androidx.compose.material3.Button
import androidx.compose.material3.Text


@Composable
fun ColumnScope.MatchThreadRoute(){//親columnのスコープ→この中でweight使える

    val viewModel: MatchThreadViewModel = hiltViewModel()


    val uiState by viewModel.uiState.collectAsStateWithLifecycle()//ViewModelのval uiStateから


    val isPosting = (uiState as? MatchThreadUiState.Success)?.isPosting ?: false


    val refreshErrorMessage =
        (uiState as? MatchThreadUiState.Success)?.refreshErrorMessage


    CommentSection(
        uiState = uiState,
        onRetry = viewModel::loadComments,
        modifier = Modifier.weight(1f)//columnの余分な部分を使える
    )



    if (refreshErrorMessage != null) {
        Text(text = refreshErrorMessage)
    }

    Button(
        onClick = viewModel::loadComments,
        enabled = !isPosting
    ) {
        Text("Refresh")
    }


    CommentPostSection(
        author = (uiState as? MatchThreadUiState.Success)?.author ?: "",
        text = (uiState as? MatchThreadUiState.Success)?.text ?: "",
        onAuthorChange = viewModel::onAuthorChange,
        onTextChange = viewModel::onTextChange,
        postErrorMessage =
            (uiState as? MatchThreadUiState.Success)?.postErrorMessage,
        isPosting = isPosting,
        onPostComment = { author, text ->
            viewModel.postComment(
                author = author,
                text = text
            )
        }


    )
}


//入れ子のUiStateからsuccessの中身を取得できない→確認して確定させる必要がある。


//owner = ViewModelを誰に所属させて、いつまで保持するか→それがNaviになってる。（コメント機能）
//Factory = ViewModelをどうやって生成するか
//Activity内→hilt対応,BackstackEntry内→デフォルトになる。hiltViewModelと明示する必要がある！。