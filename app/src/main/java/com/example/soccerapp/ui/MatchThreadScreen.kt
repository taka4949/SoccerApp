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
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D1020))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF201D3A), RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {



            Column(
                modifier = Modifier.weight(1f),//weightは親（Row)に従う。
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AsyncImage(
                    model = match.homeTeamCrest,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp)
                )
                Text(
                    text = match.homeTeam,
                    color = Color.White,
                    fontSize = 12.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "${match.homeScore ?: "-"} - ${match.awayScore ?: "-"}",
                    color = Color.White,
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(text = match.status, color = Color(0xFFBCB5CF), fontSize = 11.sp)
            }




            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AsyncImage(
                    model = match.awayTeamCrest,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp)
                )
                Text(
                    text = match.awayTeam,
                    color = Color.White,
                    fontSize = 12.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Button(//スコア更新ボタン

            onClick = onRefresh,
            enabled = !isRefreshing,
            modifier = Modifier.align(Alignment.CenterHorizontally).padding(vertical = 8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7550B8))
        ) {
            Text(if (isRefreshing) "更新中..." else "スコアを更新")
        }

        if (refreshError != null) {
            Text(
                text = refreshError,
                color = Color(0xFFFFA8B8),
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        HorizontalDivider(color = Color(0xFF38364D))
        MatchThreadRoute()
    }
}

@Composable
fun CommentSection(
    uiState: MatchThreadUiState,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    when (uiState) {
        MatchThreadUiState.Loading -> {


            Box(
                modifier = modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFF926CD5))
            }
        }
        is MatchThreadUiState.Success -> {
            LazyColumn(modifier = modifier) {


                items(uiState.comments, key = { it.id }) { comment ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = comment.author,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = comment.createdAt,
                                color = Color(0xFFBDB7CD),
                                fontSize = 11.sp
                            )
                        }
                        Text(
                            text = comment.text,
                            color = Color.White,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(top = 10.dp),
                            color = Color(0xFF38364D)
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
                Text(text = uiState.message, color = Color.White)
                Button(
                    onClick = onRetry,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7550B8))
                ) {
                    Text("再試行")
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



    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = Color.White,
        unfocusedTextColor = Color.White,
        focusedContainerColor = Color(0xFF201D3A),
        unfocusedContainerColor = Color(0xFF201D3A),
        focusedLabelColor = Color(0xFFDD9BD5),
        unfocusedLabelColor = Color(0xFFBDB7CD),
        focusedBorderColor = Color(0xFF8664CE),
        unfocusedBorderColor = Color(0xFF38364D),
        cursorColor = Color(0xFFDD9BD5)
    )





    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp)) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth().testTag("author_input"),
            value = author,
            onValueChange = onAuthorChange,
            enabled = !isPosting,
            label = { Text("Name") },
            singleLine = true,
            colors = fieldColors
        )

        OutlinedTextField(//onValueChange（中身は関数）に入る。→ViewModel→Route→再描画
        modifier = Modifier.fillMaxWidth().padding(top = 6.dp).testTag("comment_input"),
            value = text,
            onValueChange = onTextChange,
            enabled = !isPosting,
            label = { Text("Comment") },
            maxLines = 3,
            colors = fieldColors
        )

        if (postErrorMessage != null) {
            Text(text = postErrorMessage, color = Color(0xFFFFA8B8))
        }



        Button(
            modifier = Modifier.align(Alignment.End).testTag("post_button"),
            onClick = { onPostComment(author, text) },
            enabled = author.isNotBlank() && text.isNotBlank() && !isPosting,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7550B8))
        ) {
            Text("Post")
        }
    }
}





