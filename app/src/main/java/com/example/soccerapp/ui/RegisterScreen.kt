package com.example.soccerapp.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.soccerapp.RegisterViewModel
import com.example.soccerapp.ui.state.RegisterUiState

@Composable
fun RegisterScreen(
    onBack: () -> Unit,
    onRegistered: () -> Unit,
    viewModel: RegisterViewModel = hiltViewModel()
) {


    var email by rememberSaveable {
        mutableStateOf("")
    }


    var password by remember {
        mutableStateOf("")
    }


    val uiState by
    viewModel.uiState.collectAsStateWithLifecycle()



    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(
            16.dp,
            Alignment.CenterVertically
        ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {




        when (val state = uiState) {

            is RegisterUiState.Success -> {//
                Text("アカウント登録が完了しました")
                Text("メールアドレス: ${state.email}")

                Button(
                    onClick = {
                        password = ""
                        onRegistered()
                    }
                ) {
                    Text("アプリを開く")
                }
            }



            else -> {
                Text("新規アカウント登録")

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("メールアドレス") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email
                    ),

                    singleLine = true,
                    enabled = state !is RegisterUiState.Loading
                )




                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("パスワード") },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password
                    ),

                    singleLine = true,
                    enabled = state !is RegisterUiState.Loading
                )

                if (state is RegisterUiState.Loading) {
                    CircularProgressIndicator()
                }

                if (state is RegisterUiState.Error) {
                    Text(
                        text = state.message,
                        color = MaterialTheme.colorScheme.error
                    )
                }



                Button(
                    onClick = {
                        viewModel.register(//アカウント登録
                            email = email,
                            password = password
                        )
                    },
                    enabled =
                        state !is RegisterUiState.Loading &&
                                email.isNotBlank() &&
                                password.isNotBlank()
                ) {
                    Text("登録する")
                }

                TextButton(
                    onClick = onBack,
                    enabled = state !is RegisterUiState.Loading
                ) {
                    Text("戻る")
                }
            }


        }
    }


}

