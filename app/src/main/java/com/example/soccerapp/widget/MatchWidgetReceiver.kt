
package com.example.soccerapp.widget

import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver

class MatchWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = MatchWidget()
}






//Android OSから届くウィジェットの追加・更新などの通知を受け取り、MatchWidgetにつなぐ役割