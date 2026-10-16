
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
                    viewModel.dismissAccountPrompt()//登録案内画面を永続的に非表示
                }
            )
        }

        //初回のアカウント登録案内画面
        showPrompt == true && !showRegisterScreen -> {
            AccountPromptScreen(
                onRegister = {
                    showRegisterScreen = true
                },
                onSkip = viewModel::dismissAccountPrompt
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

        //ホーム選択画面（掲示板、ニュース、ランキング、アカウント登録画面）
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


    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("アカウント登録のご案内")

        Button(onClick = onRegister) {
            Text("新規登録")
        }

        Button(onClick = onSkip) {
            Text("スキップして始める")
        }
    }
}