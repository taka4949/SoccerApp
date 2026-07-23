package com.example.soccerapp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp//hiltの開始地点。メインより前に起動する土台
class SoccerApplication : Application() {
}