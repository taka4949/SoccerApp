package com.example.soccerapp.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    var email by rememberSaveable { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()



    val inputColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = Color.White,
        unfocusedTextColor = Color.White,
        disabledTextColor = Color.White,

        focusedContainerColor = Color(0xFF201D3A),
        unfocusedContainerColor = Color(0xFF201D3A),
        disabledContainerColor = Color(0xFF201D3A),

        focusedLabelColor = Color.White,
        unfocusedLabelColor = Color.White,
        disabledLabelColor = Color.White,

        focusedBorderColor = Color(0xFF9D69D4),
        unfocusedBorderColor = Color(0xFF46405F),
        cursorColor = Color.White
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF1C1731), Color(0xFF0D1020))
                )
            )
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {



        when (val state = uiState) {
            is RegisterUiState.Success -> {

                Text(text = "✓", color = Color.White, fontSize = 45.sp)


                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "アカウント登録が完了しました",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(14.dp))


                Text(text = state.email, color = Color.White)


                Spacer(modifier = Modifier.height(24.dp))


                Button(
                    onClick = {
                        password = ""
                        onRegistered()
                    },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF7550B8),
                        contentColor = Color.White
                    )
                )

                {
                    Text("ホームへ")
                }
            }



            else -> {
                Text(
                    text = "アカウント登録",
                    color = Color.White,
                    fontSize = 21.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(7.dp))
                Text(
                    text = "新規アカウントを作成",
                    color = Color.White,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(30.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("メールアドレス") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    singleLine = true,
                    enabled = state !is RegisterUiState.Loading,
                    shape = RoundedCornerShape(12.dp),
                    colors = inputColors
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("パスワード") },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    singleLine = true,
                    enabled = state !is RegisterUiState.Loading,
                    shape = RoundedCornerShape(12.dp),
                    colors = inputColors
                )

                if (state is RegisterUiState.Loading) {
                    Spacer(modifier = Modifier.height(12.dp))
                    CircularProgressIndicator(color = Color(0xFF9D69D4))
                }

                if (state is RegisterUiState.Error) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(text = state.message, color = Color(0xFFFFA8B8))
                }

                Spacer(modifier = Modifier.height(22.dp))

                Button(
                    onClick = {
                        viewModel.register(email = email, password = password)
                    },
                    enabled = state !is RegisterUiState.Loading &&
                            email.isNotBlank() && password.isNotBlank(),
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF7550B8),
                        contentColor = Color.White
                    )
                ) {
                    Text("登録する", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(
                    onClick = onBack,
                    enabled = state !is RegisterUiState.Loading
                ) {


                    Text("戻る", color = Color.White)
                }
            }
        }
    }
}


