package com.example.soccerapp.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.soccerapp.data.model.Match
import com.example.soccerapp.ui.screen.MatchThreadRoute
import com.example.soccerapp.ui.state.MatchThreadUiState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fitInside
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp

@Composable
fun  MatchThreadScreen(
    match : Match
) {
    Column {
        Text(
            text = "${match.homeTeam} vs${match.awayTeam}"
        )
        Text(
            text = "${match.homeScore} - ${match.awayScore}"
        )
        Text(
            text = match.status
        )

        Text(
            text = match.utcDate
        )

        MatchThreadRoute(match.id)
    }
}

@Composable
fun CommentSection(
    matchId : Int,
    uiState : MatchThreadUiState,
    onRetry : (Int) -> Unit,
    modifier: Modifier = Modifier
    ){

    when (uiState) {
        MatchThreadUiState.Loading -> {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        is MatchThreadUiState.Success -> {
            LazyColumn(
                modifier = modifier//weight(1f)
            ) {
                items(uiState.comments,
                    key = {it.id}//commentId→見分けられる
                ) { comment ->

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)

                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ){
                            Text(
                                text = comment.author,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = comment.createdAt,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }



                        Text(
                            text = comment.text,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                }
            }
        }

        is MatchThreadUiState.Error -> {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = uiState.message
                )

                Button(
                    onClick = {
                        onRetry(matchId)
                    }
                ) {
                    Text(
                        text = "Retry"
                    )
                }
            }
        }
    }
    }

@Composable
fun CommentPostSection(
    isPosting: Boolean,
    onPostComment: (String, String) -> Unit
) {
    var author by rememberSaveable {
        mutableStateOf("")//Tと打てば、StateOfがTを持ち→rememberが記憶→再コンポーズ→TがUIに表示
    }

    var text by rememberSaveable {
        mutableStateOf("")//stateがComposeRuntimeに通知？（深堀のちほど）
    }

    OutlinedTextField(
        value = author,
        onValueChange = { newAuthor ->
            author = newAuthor
        },
        label = {
            Text("Name")
        }
    )

    OutlinedTextField(
        value = text,
        onValueChange = { newText ->
            text = newText
        },
        label = {
            Text("Comment")
        }
    )

    Button(
        onClick = {
            if (
                author.isNotBlank() &&
                text.isNotBlank() &&
                !isPosting//ポスト中ではない時に、投稿可能
            ) {
                onPostComment(
                    author,
                    text
                )
            }
        }
    ) {
        Text("Post")//ボタンの真ん中に書いてある。
    }
}






@Preview(showBackground = true)
@Composable
fun CommentSectionLoadingPreview() {
    CommentSection(
        matchId = 1,
        uiState = MatchThreadUiState.Loading,
        onRetry = {}
    )
}