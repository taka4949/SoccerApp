package com.example.soccerapp.ui.route

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.soccerapp.MatchThreadViewModel
import com.example.soccerapp.ui.AccentPink
import com.example.soccerapp.ui.CommentPostSection
import com.example.soccerapp.ui.CommentSection
import com.example.soccerapp.ui.PrimaryText
import com.example.soccerapp.ui.state.MatchThreadUiState

@Composable
fun ColumnScope.MatchThreadRoute() {
    val viewModel: MatchThreadViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val success = uiState as? MatchThreadUiState.Success
    val isPosting = success?.isPosting ?: false

    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("コメント", color = PrimaryText, fontSize = 16.sp)
        TextButton(onClick = viewModel::loadComments, enabled = !isPosting) {
            Text("コメントを更新", color = AccentPink, fontSize = 12.sp)
        }
    }

    CommentSection(
        uiState = uiState,
        onRetry = viewModel::loadComments,
        modifier = Modifier.weight(1f)
    )

    if (success?.refreshErrorMessage != null) {
        Text(
            success.refreshErrorMessage,
            color = AccentPink,
            modifier = Modifier.padding(horizontal = 12.dp)
        )
    }

    CommentPostSection(
        author = success?.author ?: "",
        text = success?.text ?: "",
        onAuthorChange = viewModel::onAuthorChange,
        onTextChange = viewModel::onTextChange,
        postErrorMessage = success?.postErrorMessage,
        isPosting = isPosting,
        onPostComment = { author, text ->
            viewModel.postComment(author = author, text = text)
        }
    )
}
