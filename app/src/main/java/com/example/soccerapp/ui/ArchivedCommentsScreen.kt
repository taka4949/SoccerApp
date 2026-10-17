package com.example.soccerapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.soccerapp.ui.state.ArchivedCommentsUiState

@Composable
fun ArchivedCommentsScreen(
    uiState: ArchivedCommentsUiState,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize().background(ScreenBackground)
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        Text(
            "過去ログ（閲覧専用）",
            color = PrimaryText,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        when (uiState) {
            ArchivedCommentsUiState.Loading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = PrimaryPurple)
                }
            }
            is ArchivedCommentsUiState.Success -> {
                if (uiState.comments.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("コメントはありません", color = SecondaryText)
                    }
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(uiState.comments, key = { it.id }) { comment ->
                            Column(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        comment.author,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PrimaryText
                                    )
                                    Text(
                                        comment.createdAt,
                                        fontSize = 10.sp,
                                        color = SecondaryText
                                    )
                                }
                                Text(
                                    comment.text,
                                    fontSize = 14.sp,
                                    color = PrimaryText,
                                    modifier = Modifier.padding(top = 7.dp, bottom = 12.dp)
                                )
                                HorizontalDivider(color = LineColor)
                            }
                        }
                    }
                }
            }
            is ArchivedCommentsUiState.Error -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(uiState.message, color = PrimaryText)
                    Button(onClick = onRetry) { Text("再試行") }
                }
            }
        }
    }
}
