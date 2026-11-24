package com.example.soccerapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.soccerapp.PredictionViewModel
import com.example.soccerapp.ui.state.PredictionUiState



@Composable
fun PredictionScreen(
    onBack: () -> Unit,
    viewModel: PredictionViewModel = hiltViewModel()//init
) {



    val uiState by viewModel.uiState.collectAsStateWithLifecycle()



    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = Color.White,
        unfocusedTextColor = Color.White,
        focusedContainerColor = Color(0xFF201D3A),
        unfocusedContainerColor = Color(0xFF201D3A),
        focusedLabelColor = Color.White,
        unfocusedLabelColor = Color.White,
        focusedBorderColor = Color(0xFF8664CE),
        unfocusedBorderColor = Color(0xFF38364D),
        cursorColor = Color.White
    )





    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D1020))
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TextButton(onClick = onBack) {
            Text("戻る", color = Color.White)
        }



        Text(
            text = "スコア予想",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )








        when (val state = uiState) {

            PredictionUiState.Loading -> {
                CircularProgressIndicator(
                    color = Color(0xFF926CD5)
                )
            }

            is PredictionUiState.Error -> {
                Text(
                    text = state.message,
                    color = Color(0xFFFFA8B8)
                )

                Button(
                    onClick = viewModel::loadPrediction
                ) {
                    Text("再試行")
                }
            }

            is PredictionUiState.Success -> {

                val prediction = state.prediction

                if (prediction != null) {

                    Text(
                        text = "登録済みの予想",
                        color = Color.White,
                        fontSize = 18.sp
                    )

                    Text(
                        text = "${prediction.predictedHomeScore} - ${prediction.predictedAwayScore}",
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = prediction.earnedPoints?.let {
                            "獲得ポイント：${it}P"
                        } ?: "採点待ち",
                        color = Color(0xFFDD9BD5)
                    )









                } else {

                    Text(
                        text = "試合のスコアを予想してください",
                        color = Color(0xFFBDB7CD)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {




                        OutlinedTextField(
                            value = state.homeScoreInput,
                            onValueChange = { value ->
                                if (value.all { it.isDigit() }) {
                                    viewModel.onHomeScoreChange(value)
                                }
                            },
                            modifier = Modifier.weight(1f),
                            label = { Text("ホーム") },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number
                            ),
                            singleLine = true,
                            enabled = !state.isSubmitting,
                            colors = fieldColors
                        )

                        OutlinedTextField(
                            value = state.awayScoreInput,
                            onValueChange = { value ->
                                if (value.all { it.isDigit() }) {
                                    viewModel.onAwayScoreChange(value)
                                }
                            },
                            modifier = Modifier.weight(1f),
                            label = { Text("アウェイ") },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number
                            ),
                            singleLine = true,
                            enabled = !state.isSubmitting,
                            colors = fieldColors
                        )
                    }




                    if (state.submitErrorMessage != null) {
                        Text(
                            text = state.submitErrorMessage,
                            color = Color(0xFFFFA8B8)
                        )
                    }



                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = viewModel::submitPrediction,//予想の送信！！！
                        enabled = !state.isSubmitting &&
                                state.homeScoreInput.isNotBlank() &&
                                state.awayScoreInput.isNotBlank(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF7550B8)
                        )
                    ) {
                        if (state.isSubmitting) {
                            CircularProgressIndicator(
                                color = Color.White
                            )
                        } else {
                            Text("予想を確定する")
                        }
                    }
                }
            }
        }
    }
}