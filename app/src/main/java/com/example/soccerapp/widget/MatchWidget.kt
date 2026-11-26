
package com.example.soccerapp.widget

import android.content.Context
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.text.Text
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone



//Roomから試合情報を取得し、Androidのホーム画面に表示

class MatchWidget : GlanceAppWidget() {

    override suspend fun provideGlance(
        context: Context,
        id: GlanceId
    ) {




        val match = withContext(Dispatchers.IO) {//DBやファイル操作などの入出力処理に適したCoroutine Dispatcher
            val matchDao = EntryPointAccessors
                .fromApplication(
                    context.applicationContext,
                    WidgetEntryPoint::class.java
                )
                .matchDao()

            val nowUtc = SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss'Z'",
                Locale.US
            ).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }.format(Date())

            matchDao.getNextMatch(nowUtc)
        }



        //UI

        provideContent {
            Column(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text("SoccerApp")

                if (match == null) {
                    Text("開催予定の試合がありません")
                } else {
                    Text(
                        "${match.homeTeam} vs ${match.awayTeam}"
                    )
                    Text(match.utcDate)
                }
            }
        }
    }
}


//WidgetEntryPoint
//    ↓
//何を取得できるか定義
//
//EntryPointAccessors
//    ↓
//実際にその取得口へアクセス
//
//matchDao()
//    ↓
//MatchDaoを受け取る