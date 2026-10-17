package com.example.soccerapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.soccerapp.data.model.League

@Composable
fun LeagueListScreen(
    leagues: List<League>,
    onLeagueClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().background(ScreenBackground),
        contentPadding = PaddingValues(16.dp)
    ) {
        item {
            Text(
                "リーグ一覧",
                color = PrimaryText,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 18.dp)
            )
        }

        items(leagues, key = { it.id }) { league ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp)
                    .background(PanelBackground, RoundedCornerShape(14.dp))
                    .clickable { onLeagueClick(league.id) }
                    .padding(horizontal = 16.dp, vertical = 15.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = league.emblem,
                    contentDescription = null,
                    modifier = Modifier.size(44.dp)
                )
                Text(
                    text = league.name,
                    color = PrimaryText,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f).padding(start = 16.dp)
                )
                Text("›", color = SecondaryText, fontSize = 26.sp)
            }
        }
    }
}
