
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
import androidx.compose.ui.graphics.Color
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
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D1020)),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 14.dp)
    ) {
        item {
            Text(
                text = "試合一覧",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                modifier = Modifier.padding(start = 4.dp, bottom = 14.dp)
            )
        }

        items(sortedMatches) { match ->
            val statusText = when (match.status) {
                "IN_PLAY", "LIVE" -> "LIVE"
                "PAUSED" -> "HALF TIME"
                "FINISHED" -> "FT"
                "SCHEDULED", "TIMED" -> "UPCOMING"
                "POSTPONED" -> "POSTPONED"
                else -> match.status
            }

            val dateText = "${match.utcDate.substring(5, 7).toInt()}月${match.utcDate.substring(8, 10).toInt()}日 ${match.utcDate.substring(11, 16)}"

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 9.dp)
                    .background(Color(0xFF201D3A), RoundedCornerShape(12.dp))
                    .clickable { onMatchClick(match.id) }
                    .padding(horizontal = 12.dp, vertical = 12.dp)
            ) {
                Text(
                    text = dateText,
                    fontSize = 11.sp,
                    color = Color(0xFFBDB7CD),
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    AsyncImage(
                        model = match.homeTeamCrest,
                        contentDescription = null,
                        modifier = Modifier
                            .size(34.dp)
                            .background(Color(0xFFEAE8F2), RoundedCornerShape(7.dp))
                            .padding(3.dp)
                    )
                    Text(
                        text = match.homeTeam,
                        color = Color.White,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f).padding(horizontal = 5.dp)
                    )
                    Text(
                        text = " VS ",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = match.awayTeam,
                        color = Color.White,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f).padding(horizontal = 5.dp)
                    )



                    AsyncImage(
                        model = match.awayTeamCrest,
                        contentDescription = null,
                        modifier = Modifier
                            .size(34.dp)
                            .background(Color(0xFFEAE8F2), RoundedCornerShape(7.dp))
                            .padding(3.dp)
                    )
                }
                Text(
                    text = statusText,
                    color = if (statusText == "LIVE") Color(0xFFF48DC6)
                    else Color(0xFFAAA5BD),
                    fontSize = 10.sp,
                    modifier = Modifier.align(Alignment.End).padding(top = 4.dp)
                )
            }
        }
    }
}

//modifier=Composable関数自体への設定