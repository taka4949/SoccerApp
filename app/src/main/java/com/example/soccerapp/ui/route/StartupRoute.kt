
package com.example.soccerapp.ui.route

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.soccerapp.StartupViewModel
import com.example.soccerapp.ui.HomeScreen
import com.example.soccerapp.ui.screen.RegisterScreen
import androidx.compose.material3.AlertDialog
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun StartupRoute(
    viewModel: StartupViewModel = hiltViewModel()
) {

    val showPrompt by
    viewModel.showAccountPrompt.collectAsStateWithLifecycle()

    var showRegisterScreen by rememberSaveable {
        mutableStateOf(false)
    }

    var showBoardScreen by rememberSaveable {
        mutableStateOf(false)
    }

    when {

        //DataStoreの読み込み中
        showPrompt == null -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }


        //新規登録ボタン→アカウント登録画面
        showRegisterScreen -> {
            RegisterScreen(
                onBack = {
                    showRegisterScreen = false//戻る
                },
                onRegistered = {
                    showRegisterScreen = false//登録後に、この画面を非表示
                    viewModel.dismissAccountPrompt()//登録後に、登録案内画面を永続的に非表示
                }
            )
        }

        //初回のアカウント登録案内画面
        showPrompt == true && !showRegisterScreen -> {
            AccountPromptScreen(
                onRegister = {
                    showRegisterScreen = true
                },
                onSkip = viewModel::dismissAccountPrompt//登録案内を非表示
            )
        }



        //掲示板画面
        showPrompt == false && showBoardScreen -> {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                TextButton(
                    onClick = {
                        showBoardScreen = false
                    }
                ) {
                    Text("← ホーム")
                }

                Box(
                    modifier = Modifier.weight(1f)
                ) {
                    MainRoute()//リーグ選択画面
                }
            }
        }



        //ホーム選択画面（掲示板、ニュース、ランキング、アカウント登録画面）アカウント登録、スキップ後に表示。
        showPrompt == false && !showBoardScreen -> {
            HomeScreen(
                onBoardClick = {//掲示板へ（true)
                    showBoardScreen = true
                },
                onAccountClick = {//アカウント登録
                    showRegisterScreen = true
                }
            )
        }
    }
}


@Composable
private fun AccountPromptScreen(
    onRegister: () -> Unit,
    onSkip: () -> Unit
) {


    var showConfirmation by rememberSaveable {
        mutableStateOf(false)
    }

    val primaryColor = Color(0xFF7352B5)
    val secondaryColor = Color(0xFF27203D)
    val textColor = Color(0xFFB9B1CD)



    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(//背景にグラデーション付ける
                    colors = listOf(
                        Color(0xFF211535),
                        Color(0xFF111323)
                    )
                )
            )
            .padding(horizontal = 24.dp),//左右に余白
        contentAlignment = Alignment.Center
    ) {


        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "サッカーを、もっと楽しもう。",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = "アカウント登録で、試合ごとの\nコメントに参加できます。",
                fontSize = 14.sp,
                lineHeight = 22.sp,
                color = textColor,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(36.dp))

            Button(
                onClick = onRegister,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),//ボタンそのものの縦の長さ
                shape = RoundedCornerShape(12.dp),//ボタンの形状をシャープへ
                colors = ButtonDefaults.buttonColors(//ボタン色指定
                    containerColor = primaryColor,
                    contentColor = Color.White
                )
            ) {
                Text("新規登録")
            }

            Spacer(Modifier.height(12.dp))





            OutlinedButton(//控えめな操作
                onClick = {
                    showConfirmation = true
                },
                modifier = Modifier//指定する順番で見た目やレイアウトの結果が変わる
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(
                    1.dp,
                    Color(0xFF54466D)
                ),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = secondaryColor,
                    contentColor = Color.White
                )
            ) {

                Text("スキップして始める")//ダイアログを表示へ
            }
        }
    }



    if (showConfirmation) {
        AlertDialog(
            onDismissRequest = {
                showConfirmation = false//ダイアログ内のボタン外タップ→ダイアログ非表示
            },
            title = {
                Text("スキップしますか？")
            },
            text = {
                Text("次回から登録案内は表示されません。")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showConfirmation = false
                        onSkip()
                    }
                ) {
                    Text("はい")
                }
            },




            dismissButton = {
                TextButton(
                    onClick = {
                        showConfirmation = false
                    }
                ) {
                    Text("いいえ")
                }
            },
            containerColor = Color(0xFF27203D),
            titleContentColor = Color.White,
            textContentColor = textColor
        )
    }
}



