package com.example.soccerapp.ui

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import com.example.soccerapp.data.model.League
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import com.example.soccerapp.data.model.Match
import androidx.compose.foundation.clickable
import androidx.compose.ui.Modifier


@Composable
fun LeagueListScreen(
    leagues: List<League>,
    matches : List<Match>,
    onMatchClick : (Int) -> Unit
) {
    LazyColumn {
        leagues.forEach { league ->

            item{//itemは単一の要素を表示する
                Text(text = league.name)
            }

            val leagueMatches = matches.filter { it.leagueId == league.id }

            items(leagueMatches){match ->
                Text(text = " ${match.homeTeam} vs ${match.awayTeam}",
                    modifier = Modifier.clickable{
                        onMatchClick(match.id)
                    }
                )
            }//itemsは複数の要素を表示する
        }

    }
}