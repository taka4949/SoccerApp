package com.example.soccerapp.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.soccerapp.data.model.Match


@Composable
fun MatchListScreen(
    matches: List<Match>,
    onMatchClick: (Int) -> Unit
) {
    LazyColumn {
        items(matches) { match ->
            Text(
                text = "${match.homeTeam} vs ${match.awayTeam}",
                modifier = Modifier.clickable {
                    onMatchClick(match.id)
                }
            )
        }
    }
}