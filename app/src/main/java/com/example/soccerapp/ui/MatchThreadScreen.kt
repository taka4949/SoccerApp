package com.example.soccerapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.soccerapp.data.model.Match
import com.example.soccerapp.ui.route.MatchThreadRoute
import com.example.soccerapp.ui.state.MatchThreadUiState

@Composable
fun MatchThreadScreen(
    match: Match,
    isRefreshing: Boolean,
    refreshError: String?,
    onRefresh: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().background(ScreenBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    PanelBackground,
                    RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
                )
                .padding(horizontal = 12.dp, vertical = 18.dp)
        ) {
            Text(
                "試合詳細",
                color = SecondaryText,
                fontSize = 13.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 15.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AsyncImage(
                        model = match.homeTeamCrest,
                        contentDescription = null,
                        modifier = Modifier.size(54.dp)
                    )
                    Text(
                        match.homeTeam,
                        color = PrimaryText,
                        fontSize = 12.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "${match.homeScore ?: "-"} - ${match.awayScore ?: "-"}",
                        color = PrimaryText,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(match.status, color = AccentPink, fontSize = 11.sp)
                }
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AsyncImage(
                        model = match.awayTeamCrest,
                        contentDescription = null,
                        modifier = Modifier.size(54.dp)
                    )
                    Text(
                        match.awayTeam,
                        color = PrimaryText,
                        fontSize = 12.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            Button(
                onClick = onRefresh,
                enabled = !isRefreshing,
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryPurple,
                    contentColor = Color.White
                ),
                modifier = Modifier.align(Alignment.CenterHorizontally)
                    .padding(top = 14.dp)
            ) {
                Text(if (isRefreshing) "更新中..." else "スコアを更新")
            }
            if (refreshError != null) {
                Text(
                    refreshError,
                    color = Color(0xFFFF9DAB),
                    modifier = Modifier.padding(top = 6.dp)
                )
            }
        }
        MatchThreadRoute()
    }
}

@Composable
fun CommentSection(
    uiState : MatchThreadUiState,
    onRetry : () -> Unit,
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
                        onRetry()
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
    author: String,
    text: String,
    onAuthorChange: (String) -> Unit,
    onTextChange: (String) -> Unit,
    isPosting: Boolean,
    postErrorMessage: String?,
    onPostComment: (String, String) -> Unit
) {



    OutlinedTextField(
        modifier = Modifier.testTag("author_input"),
        value = author,
        onValueChange = onAuthorChange,
        enabled = !isPosting,
        label = { Text("Name") }
    )


    OutlinedTextField(
        modifier = Modifier.testTag("comment_input"),
        value = text,
        onValueChange = onTextChange,//onValueChange（中身は関数）に入る。→ViewModel→Route→再描画
        enabled = !isPosting,
        label = {
            Text("Comment")
        }
    )

    if (postErrorMessage != null) {
        Text(
            text = postErrorMessage,
            color = MaterialTheme.colorScheme.error
        )
    }


    Button(
        modifier = Modifier.testTag("post_button"),
        onClick = {
            onPostComment(author, text)
        },
        enabled =
            author.isNotBlank() &&
                    text.isNotBlank() &&
                    !isPosting
    ) {
        Text("Post")//ボタンの真ん中に書いてある。関数の最後の引数がラムダ→（）の外に出せる。
    }
}






