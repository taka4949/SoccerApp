package com.example.soccerapp.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import com.example.soccerapp.data.model.League
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.layout.PaddingValues


@Composable
fun LeagueListScreen(
    leagues: List<League>,
    onLeagueClick : (String) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(
            vertical = 8.dp
        )
    ) {
        items(leagues) { league ->
            Column {//縦
                Row(//横
                    modifier = Modifier
                        .fillMaxWidth()//横幅いっぱい（順番大事、横幅→クリックも余白に有効→縦と横に余白）
                        .clickable {
                            onLeagueClick(league.id)//loadMatch()へ→Repository→試合データもってくる。
                        }
                        .padding(
                            horizontal = 20.dp,
                            vertical = 16.dp
                        ),
                    verticalAlignment = Alignment.CenterVertically//Row内の部品を中央へ寄せる。
                ) {
                    Text(
                        text = league.name,
                        modifier = Modifier
                            .weight(1f)//これは余白を使う。
                            .padding(end = 12.dp),
                        style = MaterialTheme.typography.bodyLarge,
                        maxLines = 1,//一行に収める
                        overflow = TextOverflow.Ellipsis//長文は...に省略。
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