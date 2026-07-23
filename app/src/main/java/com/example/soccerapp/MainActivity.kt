package com.example.soccerapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.soccerapp.navigation.AppNavigation
import com.example.soccerapp.ui.screen.MainRoute
import dagger.hilt.android.AndroidEntryPoint



@AndroidEntryPoint//hilt対応のオブジェクトを受け取るために記入必須（最初のみ）
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize().safeDrawingPadding()) {
                 MainRoute()
                }
            }
        }
    }
}