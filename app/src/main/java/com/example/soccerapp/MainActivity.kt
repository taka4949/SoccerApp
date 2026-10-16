
package com.example.soccerapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.soccerapp.ui.route.StartupRoute
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay

@AndroidEntryPoint//hilt対応のオブジェクトを受け取るために記入必須（最初のみ）
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {

                var showStartupImage by rememberSaveable {
                    mutableStateOf(true)
                }

                LaunchedEffect(Unit) {
                    delay(1500)
                    showStartupImage = false
                }



                Surface(//アプリ全体の土台、設定。
                    modifier = Modifier
                        .fillMaxSize()
                        .safeDrawingPadding()
                ) {
                    if (showStartupImage) {
                        StartupImageScreen()
                    } else {
                        StartupRoute()
                    }
                }
            }
        }
    }
}

@Composable
private fun StartupImageScreen() {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(
                id = R.drawable.startup_art
            ),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .aspectRatio(1f),
            contentScale = ContentScale.Fit
        )
    }
}
