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
import com.example.soccerapp.ui.AccentPink
import com.example.soccerapp.ui.InputBackground
import com.example.soccerapp.ui.LineColor
import com.example.soccerapp.ui.PrimaryPurple
import com.example.soccerapp.ui.PrimaryText
import com.example.soccerapp.ui.ScreenBackground
import com.example.soccerapp.ui.SecondaryText
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
    val state = uiState

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF211535), ScreenBackground)
                )
            )
            .padding(horizontal = 26.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (state is RegisterUiState.Success) {
            Text("✓", fontSize = 48.sp, color = AccentPink)
            Spacer(Modifier.height(14.dp))
            Text(
                "アカウント登録が完了しました",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryText,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(14.dp))
            Text(state.email, fontSize = 14.sp, color = SecondaryText)
            Spacer(Modifier.height(22.dp))
            Button(
                onClick = {
                    password = ""
                    onRegistered()
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryPurple,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("アプリを開く")
            }
        } else {
            Text(
                "アカウント登録",
                color = PrimaryText,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(26.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("メールアドレス") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true,
                enabled = state !is RegisterUiState.Loading,
                colors = formColors()
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("パスワード") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true,
                enabled = state !is RegisterUiState.Loading,
                colors = formColors()
            )
            if (state is RegisterUiState.Loading) {
                Spacer(Modifier.height(12.dp))
                CircularProgressIndicator(color = AccentPink)
            }
            if (state is RegisterUiState.Error) {
                Spacer(Modifier.height(10.dp))
                Text(state.message, color = Color(0xFFFFA3B0))
            }
            Spacer(Modifier.height(22.dp))
            Button(
                onClick = { viewModel.register(email = email, password = password) },
                enabled = state !is RegisterUiState.Loading &&
                    email.isNotBlank() && password.isNotBlank(),
                modifier = Modifier.fillMaxWidth().height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryPurple,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("登録する")
            }
            Spacer(Modifier.height(12.dp))
            TextButton(onClick = onBack, enabled = state !is RegisterUiState.Loading) {
                Text("戻る", color = SecondaryText)
            }
        }
    }
}

@Composable
private fun formColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = PrimaryText,
    unfocusedTextColor = PrimaryText,
    disabledTextColor = SecondaryText,
    focusedLabelColor = AccentPink,
    unfocusedLabelColor = SecondaryText,
    disabledLabelColor = SecondaryText,
    focusedBorderColor = PrimaryPurple,
    unfocusedBorderColor = LineColor,
    focusedContainerColor = InputBackground,
    unfocusedContainerColor = InputBackground,
    disabledContainerColor = InputBackground,
    cursorColor = AccentPink
)
