package com.example.soccerapp.ui

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import com.example.soccerapp.data.model.League
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.foundation.clickable
import androidx.compose.ui.Modifier


@Composable
fun LeagueListScreen(
    leagues: List<League>,
    onLeagueClick : (String) -> Unit
) {
    LazyColumn {
        items(leagues) { league ->
            Text(
                text = league.name,
                modifier = Modifier.clickable {
                    onLeagueClick(league.id)
                }
            )
        }
    }
}