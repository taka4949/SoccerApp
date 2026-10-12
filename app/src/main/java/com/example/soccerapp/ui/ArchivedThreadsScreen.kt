package com.example.soccerapp.ui

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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
            .padding(16.dp)
    ) {



        Text(
            text = "過去ログ一覧",
            style = MaterialTheme.typography.titleLarge
        )



        Spacer(modifier = Modifier.height(16.dp))





        when (uiState) {

            ArchivedThreadsUiState.Loading -> {

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is ArchivedThreadsUiState.Success -> {

                if (uiState.threads.isEmpty()) {

                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "過去ログはまだありません"
                        )
                    }

                } else {

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {

                        items(
                            items = uiState.threads,
                            key = { thread -> thread.threadId }
                        ) { thread ->

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = {
                                    onThreadClick(thread.threadId)
                                }
                            ) {

                                Text(
                                    text = "過去ログ ${thread.number}",
                                    modifier = Modifier.padding(16.dp),
                                    style = MaterialTheme.typography.titleMedium
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

                    Text(
                        text = uiState.message
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = onRetry
                    ) {
                        Text("Retry")
                    }
                }
            }
        }
    }
}

