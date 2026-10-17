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
import androidx.compose.ui.graphics.Color
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
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D1020)),
        contentPadding = PaddingValues(16.dp)
    ) {
        item {
            Text(
                text = "リーグ一覧",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }




        items(leagues) { league ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp)
                    .background(Color(0xFF201D3A), RoundedCornerShape(12.dp))
                    .clickable { onLeagueClick(league.id) }
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = league.emblem,
                    contentDescription = null,
                    modifier = Modifier
                        .size(44.dp)
                        .background(Color(0xFFEAE8F2), RoundedCornerShape(8.dp))
                        .padding(4.dp)
                )
                Text(
                    text = league.name,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 14.dp),
                    color = Color.White,
                    fontSize = 15.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text("›", color = Color(0xFFC6B9D9), fontSize = 23.sp)
            }
        }
    }
}





//modifier=Composable関数自体への設定