package com.example.soccerapp.ui



import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.soccerapp.ui.state.ArchivedThreadsUiState

@Composable
fun ArchivedThreadsScreen(
    uiState: ArchivedThreadsUiState,
    onThreadClick: (Long) -> Unit,
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
            text = "過去ログ一覧",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )



        Spacer(modifier = Modifier.height(16.dp))




        when (uiState) {
            ArchivedThreadsUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF926CD5))
                }
            }
            is ArchivedThreadsUiState.Success -> {
                if (uiState.threads.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("過去ログはまだありません", color = Color(0xFFBDB7CD))
                    }
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(uiState.threads, key = { it.threadId }) { thread ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = { onThreadClick(thread.threadId) },
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF201D3A))
                            ) {
                                Text(
                                    text = "過去ログ ${thread.number}   ›",
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    modifier = Modifier.padding(18.dp)
                                )
                            }
                        }
                    }
                }
            }



            is ArchivedThreadsUiState.Error -> {
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