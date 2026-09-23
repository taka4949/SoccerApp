package com.example.soccerapp.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.soccerapp.data.model.Match
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height


@Composable
fun MatchListScreen(
    matches: List<Match>,
    onMatchClick: (Int) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(
            vertical = 8.dp
        )
    ) {
        items(matches) { match ->
            Column {//縦
                Row(//横
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onMatchClick(match.id)
                        }
                        .padding(
                            horizontal = 20.dp,
                            vertical = 16.dp
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${match.homeTeam} vs ${match.awayTeam}",
                        modifier = Modifier
                            .weight(1f)//これは余白を使う。
                            .padding(end = 12.dp),
                        style = MaterialTheme.typography.bodyLarge,
                        maxLines = 1,//一行に収める
                        overflow = TextOverflow.Ellipsis
                        )
                    Text(
                        text = ">"
                    )
                }



                HorizontalDivider(
                    modifier = Modifier.padding(start = 20.dp),
                    thickness = 1.dp,//線の太さ
                    color = MaterialTheme.colorScheme.outlineVariant
                )
            }
    }
    }
}



//modifier=Composable関数自体への設定