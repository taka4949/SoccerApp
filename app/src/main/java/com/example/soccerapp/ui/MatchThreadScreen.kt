package com.example.soccerapp.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.soccerapp.data.model.Match
import com.example.soccerapp.ui.screen.MatchThreadRoute
import com.example.soccerapp.ui.state.MatchThreadUiState

@Composable
fun MatchThreadScreen(
    match: Match
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
    matchId: Int,
    uiState: MatchThreadUiState,
    onRetry: (Int) -> Unit,
    modifier: Modifier = Modifier
) {

    when (uiState) {
        MatchThreadUiState.Loading -> {
            Box(
                modifier = modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        is MatchThreadUiState.Success -> {
            LazyColumn(
                modifier = modifier.fillMaxWidth(),
                contentPadding = PaddingValues(
                    horizontal = 12.dp,
                    vertical = 8.dp
                ),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    items = uiState.comments,
                    key = { it.id }
                ) { comment ->
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = comment.author,
                            style = MaterialTheme.typography.labelLarge
                        )
                        Text(
                            text = comment.text,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = comment.createdAt,
                            style = MaterialTheme.typography.bodySmall
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
                modifier = modifier.fillMaxWidth(),
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
        mutableStateOf("")
    }

    var text by rememberSaveable {
        mutableStateOf("")
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 12.dp,
                vertical = 8.dp
            ),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value = author,
            onValueChange = { newAuthor ->
                author = newAuthor
            },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text("Name")
            },
            singleLine = true
        )

        OutlinedTextField(
            value = text,
            onValueChange = { newText ->
                text = newText
            },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text("Comment")
            },
            minLines = 2
        )

        Button(
            onClick = {
                if (
                    author.isNotBlank() &&
                    text.isNotBlank() &&
                    !isPosting
                ) {
                    onPostComment(
                        author,
                        text
                    )
                }
            },
            modifier = Modifier.align(Alignment.End),
            enabled = !isPosting
        ) {
            Text(
                text = if (isPosting) "Posting..." else "Post"
            )
        }
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
