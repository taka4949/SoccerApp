
package com.example.soccerapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
    onArchivesClick: () -> Unit,
    onPredictionClick: () -> Unit
) {



    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D1020))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = "試合メニュー",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(6.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        listOf(Color(0xFF3A2352), Color(0xFF252444))
                    ),
                    RoundedCornerShape(14.dp)
                )
                .clickable(onClick = onLiveClick)
                .padding(22.dp)
        ) {
            Text(
                text = "交流掲示板 >",
                color = Color.White,
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                "試合についてコメントする",
                color = Color(0xFFD3C7DF),
                fontSize = 12.sp
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        listOf(Color(0xFF292543), Color(0xFF47264D))
                    ),
                    RoundedCornerShape(14.dp)
                )
                .clickable(onClick = onArchivesClick)
                .padding(22.dp)
        ) {
            Text(
                "過去ログ  ›",
                color = Color.White,
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                "過去のコメントを閲覧する",
                color = Color(0xFFD3C7DF),
                fontSize = 12.sp
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        listOf(Color(0xFF30305A), Color(0xFF463069))
                    ),
                    RoundedCornerShape(14.dp)
                )
                .clickable(onClick = onPredictionClick)
                .padding(22.dp)
        ) {
            Text(
                "スコア予想  ›",
                color = Color.White,
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                "試合結果を予想してポイントを獲得",
                color = Color(0xFFD3C7DF),
                fontSize = 12.sp
            )
        }
    }
}


