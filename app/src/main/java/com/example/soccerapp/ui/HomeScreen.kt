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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HomeScreen(
    onBoardClick: () -> Unit,
    onAccountClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBackground)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("ホーム", color = PrimaryText, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            TextButton(onClick = onAccountClick) {
                Text("アカウント登録", color = AccentPink)
            }
        }

        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(104.dp)
                .background(
                    Brush.horizontalGradient(
                        listOf(Color(0xFF53234D), Color(0xFF1C264B))
                    ),
                    RoundedCornerShape(16.dp)
                )
                .clickable(onClick = onBoardClick)
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("掲示板", color = PrimaryText, fontSize = 19.sp, fontWeight = FontWeight.Bold)
                Text("試合ごとの交流・過去ログを閲覧", color = SecondaryText, fontSize = 12.sp)
            }
            Text("›", color = PrimaryText, fontSize = 26.sp)
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(104.dp)
                .background(
                    Brush.horizontalGradient(
                        listOf(Color(0xFF382044), Color(0xFF1C264B))
                    ),
                    RoundedCornerShape(16.dp)
                )
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("ニュース", color = PrimaryText, fontSize = 19.sp, fontWeight = FontWeight.Bold)
                Text("最新のサッカー情報", color = SecondaryText, fontSize = 12.sp)
            }
            Text("›", color = SecondaryText, fontSize = 26.sp)
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(104.dp)
                .background(
                    Brush.horizontalGradient(
                        listOf(Color(0xFF54234E), Color(0xFF1C264B))
                    ),
                    RoundedCornerShape(16.dp)
                )
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("予想ランキング", color = PrimaryText, fontSize = 19.sp, fontWeight = FontWeight.Bold)
                Text("予想ポイントを競おう", color = SecondaryText, fontSize = 12.sp)
            }
            Text("›", color = SecondaryText, fontSize = 26.sp)
        }
    }
}
