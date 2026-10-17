package com.example.soccerapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MatchMenuScreen(
    onLiveClick: () -> Unit,
    onArchivesClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBackground)
            .padding(16.dp)
    ) {
        Text(
            "掲示板",
            color = PrimaryText,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(24.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(156.dp)
                .background(
                    Brush.horizontalGradient(
                        listOf(Color(0xFF4D2354), Color(0xFF26234C), Color(0xFF702858))
                    ),
                    RoundedCornerShape(16.dp)
                )
                .clickable(onClick = onLiveClick)
                .padding(22.dp)
        ) {
            Spacer(Modifier.weight(1f))
            Text("交流掲示板   ›", color = PrimaryText, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text("試合ごとのコメントを見る・投稿する", color = SecondaryText, fontSize = 12.sp)
        }
        Spacer(Modifier.height(14.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(156.dp)
                .background(
                    Brush.horizontalGradient(
                        listOf(Color(0xFF28234A), Color(0xFF533060), Color(0xFF782852))
                    ),
                    RoundedCornerShape(16.dp)
                )
                .clickable(onClick = onArchivesClick)
                .padding(22.dp)
        ) {
            Spacer(Modifier.weight(1f))
            Text("過去ログ   ›", color = PrimaryText, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text("以前のコメントを閲覧する", color = SecondaryText, fontSize = 12.sp)
        }
    }
}
