package com.example.soccerapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.soccerapp.data.model.Match

@Composable
fun MatchListScreen(
    matches: List<Match>,
    onMatchClick: (Int) -> Unit
) {
    val sortedMatches = matches.sortedBy { it.utcDate }

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(ScreenBackground),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 14.dp)
    ) {
        item {
            Text(
                "試合一覧",
                color = PrimaryText,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 6.dp, bottom = 14.dp)
            )
        }

        items(sortedMatches, key = { it.id }) { match ->
            val statusText = when (match.status) {
                "IN_PLAY", "LIVE" -> "LIVE"
                "PAUSED" -> "HALF TIME"
                "FINISHED" -> "FT"
                "SCHEDULED", "TIMED" -> "予定"
                "POSTPONED" -> "延期"
                else -> match.status
            }
            val dateText = if (match.utcDate.length >= 16) {
                "${match.utcDate.substring(5, 7).toIntOrNull() ?: 0}月" +
                    "${match.utcDate.substring(8, 10).toIntOrNull() ?: 0}日  " +
                    match.utcDate.substring(11, 16)
            } else {
                match.utcDate
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 9.dp)
                    .background(PanelBackground, RoundedCornerShape(13.dp))
                    .clickable { onMatchClick(match.id) }
                    .padding(horizontal = 12.dp, vertical = 10.dp)
            ) {
                Text(
                    dateText,
                    fontSize = 11.sp,
                    color = SecondaryText,
                    modifier = Modifier.padding(bottom = 10.dp)
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AsyncImage(
                        model = match.homeTeamCrest,
                        contentDescription = null,
                        modifier = Modifier.size(36.dp)
                    )
                    Text(
                        match.homeTeam,
                        color = PrimaryText,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f).padding(horizontal = 4.dp)
                    )
                    Text(
                        text = if (match.homeScore != null && match.awayScore != null) {
                            "${match.homeScore} - ${match.awayScore}"
                        } else {
                            "VS"
                        },
                        color = PrimaryText,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        match.awayTeam,
                        color = PrimaryText,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f).padding(horizontal = 4.dp)
                    )
                    AsyncImage(
                        model = match.awayTeamCrest,
                        contentDescription = null,
                        modifier = Modifier.size(36.dp)
                    )
                    Text(
                        statusText,
                        color = if (statusText == "LIVE") AccentPink else SecondaryText,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                    Text("›", color = SecondaryText, fontSize = 19.sp)
                }
            }
        }
    }
}
