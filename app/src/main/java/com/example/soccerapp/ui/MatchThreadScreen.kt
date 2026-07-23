package com.example.soccerapp.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.soccerapp.data.model.Match



@Composable
fun  MatchThreadScreen(
    match : Match
) {
    Column{
        Text(
            text = "${match.homeTeam} vs${match.awayTeam}"
        )
        Text(
            text = "${match.homeScore} - ${match.awayScore}"
        )
        Text(
            text = match.matchTime.toString()
        )

    }

}