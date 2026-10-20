package com.example.soccerapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HomeScreen(
    onBoardClick: () -> Unit,
    onAccountClick: () -> Unit,
    isLoggedIn: Boolean = false
) {

    val background = Color(0xFF0D1020)
    val textColor = Color(0xFFFFFFFF)
    val subTextColor = Color(0xFFC5BCD6)
    val accentColor = Color(0xFFE58AC4)
    val cardShape = RoundedCornerShape(16.dp)




    //未定（画面下部にNavi保留）





Column(
modifier = Modifier
.fillMaxSize()
.background(background)
.padding(horizontal = 16.dp, vertical = 12.dp),
verticalArrangement = Arrangement.spacedBy(12.dp)
) {




    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {


        Text(
            text = "ホーム",
            color = textColor,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )



        if (!isLoggedIn) {
            TextButton(onClick = onAccountClick) {
                Text(
                    text = "アカウント登録",
                    color = accentColor
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(8.dp))

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(104.dp)
            .clip(cardShape)
            .background(
                Brush.horizontalGradient(
                    listOf(Color(0xFF53234D), Color(0xFF1C264B))
                )
            )
            .clickable(onClick = onBoardClick)
            .padding(18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "掲示板",
                color = textColor,
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "試合ごとの交流・過去ログを閲覧",
                color = subTextColor,
                fontSize = 12.sp
            )
        }
        Text("›", color = textColor, fontSize = 26.sp)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(104.dp)
            .clip(cardShape)
            .background(
                Brush.horizontalGradient(
                    listOf(Color(0xFF382044), Color(0xFF1C264B))
                )
            )
            .padding(18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "ニュース",
                color = textColor,
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "最新のサッカー情報",
                color = subTextColor,
                fontSize = 12.sp
            )
        }
        Text("›", color = subTextColor, fontSize = 26.sp)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(104.dp)
            .clip(cardShape)
            .background(
                Brush.horizontalGradient(
                    listOf(Color(0xFF54234E), Color(0xFF1C264B))
                )
            )
            .padding(18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "予想ランキング",
                color = textColor,
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "予想ポイントを競おう",
                color = subTextColor,
                fontSize = 12.sp
            )
        }
        Text("›", color = subTextColor, fontSize = 26.sp)
    }
}
}