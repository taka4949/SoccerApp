package com.example.soccerapp.data.repository

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject



private val Context.dataStore by preferencesDataStore(
    name = "user_preferences"
)

class UserPreferencesRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val hasSeenPromptKey =
        booleanPreferencesKey("has_seen_account_prompt")//Key（map)

    val hasSeenAccountPrompt: Flow<Boolean> =
        context.dataStore.data.map { preferences ->
            preferences[hasSeenPromptKey] ?: false//保存された値を読み取り、変更されたとき新しい値受け取る
        }

    suspend fun markPromptAsSeen() {//これ呼ばれたらスキップ
        context.dataStore.edit { preferences ->
            preferences[hasSeenPromptKey] = true//値を保存
        }
    }
}

//activityContext:画面表示やテーマなど、Activityに依存する機能を扱う際に適している。
//applicationContextは、 特定のMainActivityインスタンスを指していない。
//そのため、Activityが作り直されても、同じアプリプロセス内では引き続きアプリケーション側のContextを利用できる。
//Activityの生存期間を超えて、そのActivityへの参照を保持し続ける→Contextの不適切な保持によってメモリリーク