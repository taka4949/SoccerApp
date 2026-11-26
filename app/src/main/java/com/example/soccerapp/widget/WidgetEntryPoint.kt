
package com.example.soccerapp.widget

import com.example.soccerapp.data.local.dao.MatchDao
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface WidgetEntryPoint {
    fun matchDao(): MatchDao //Hiltから取得
}


//EntryPointは、Hiltが管理している依存関係を外部から取得するための入口