
package com.example.soccerapp.ui


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0D1020))
            .padding(16.dp)
    ) {
        Text(
            text = "過去ログ（閲覧専用）",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )



        Spacer(modifier = Modifier.height(16.dp))

        when (uiState) {
            ArchivedCommentsUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF926CD5))
                }
            }



            is ArchivedCommentsUiState.Success -> {
                if (uiState.comments.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("コメントはありません", color = Color(0xFFBDB7CD))
                    }
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(uiState.comments, key = { it.id }) { comment ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = comment.author,
                                        color = Color.White,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
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
                                    modifier = Modifier.padding(vertical = 10.dp)
                                )
                                HorizontalDivider(color = Color(0xFF37344E))
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
                    Text(text = uiState.message, color = Color.White)
                    Spacer(modifier = Modifier.height(12.dp))
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
}


