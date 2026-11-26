package com.example.soccerapp.widget

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.appWidgetBackground
import androidx.glance.background
import androidx.glance.color.ColorProvider
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
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

        val match = withContext(Dispatchers.IO) { //DBやファイル操作などの入出力処理に適したCoroutine Dispatcher
            val favoriteMatchDao = EntryPointAccessors
                .fromApplication(
                    context.applicationContext,
                    WidgetEntryPoint::class.java
                )
                .favoriteMatchDao()

            val nowUtc = SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss'Z'",
                Locale.US
            ).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }.format(Date())

            favoriteMatchDao.getNextFavoriteMatch(nowUtc)
        }

        //UI

        val white = ColorProvider(
            day = Color.White,
            night = Color.White
        )

        val yellow = ColorProvider(
            day = Color(0xFFFACC15),
            night = Color(0xFFFACC15)
        )

        val blue = ColorProvider(
            day = Color(0xFF60A5FA),
            night = Color(0xFF60A5FA)
        )

        val gray = ColorProvider(
            day = Color(0xFFCBD5E1),
            night = Color(0xFFCBD5E1)
        )

        provideContent {
            Column(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .background(Color(0xFF0F172A))
                    .appWidgetBackground()
                    .cornerRadius(20.dp)
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "★ お気に入りの次の試合",
                    style = TextStyle(
                        color = yellow,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                )

                Spacer(modifier = GlanceModifier.height(10.dp))

                if (match == null) {
                    Text(
                        text = "開催予定のお気に入り試合がありません",
                        modifier = GlanceModifier.fillMaxWidth(),
                        style = TextStyle(
                            color = white,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                    )
                } else {
                    Text(
                        text = match.homeTeam,
                        modifier = GlanceModifier.fillMaxWidth(),
                        style = TextStyle(
                            color = white,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    )

                    Text(
                        text = "VS",
                        style = TextStyle(
                            color = blue,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )

                    Text(
                        text = match.awayTeam,
                        modifier = GlanceModifier.fillMaxWidth(),
                        style = TextStyle(
                            color = white,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    )

                    Spacer(modifier = GlanceModifier.height(10.dp))

                    Text(
                        text = formatToJst(match.utcDate),
                        style = TextStyle(
                            color = gray,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                    )
                }
            }
        }
    }

    private fun formatToJst(utcDate: String): String {
        return runCatching {
            val inputFormat = SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss'Z'",
                Locale.US
            ).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }

            val outputFormat = SimpleDateFormat(
                "M月d日（E）HH:mm",
                Locale.JAPAN
            ).apply {
                timeZone = TimeZone.getTimeZone("Asia/Tokyo")
            }

            val date = inputFormat.parse(utcDate)
                ?: return utcDate

            outputFormat.format(date) + " JST"
        }.getOrDefault(utcDate)
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