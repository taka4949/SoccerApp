package com.example.soccerapp.ui



import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(
    onBoardClick: () -> Unit,
    onAccountClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {

        //アカウント登録
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(
                onClick = onAccountClick
            ) {
                Text("アカウント登録")
            }
        }



        //掲示板
        Button(
            onClick = onBoardClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("掲示板")
        }



        //ニュース（現時点では文字のみ）
        Text(
            text = "ニュース",
            style = MaterialTheme.typography.titleLarge
        )




        //予想ランキング（現時点では文字のみ）
        Text(
            text = "予想ランキング",
            style = MaterialTheme.typography.titleLarge
        )
    }
}