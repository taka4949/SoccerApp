
package com.example.soccerapp.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.soccerapp.data.local.TokenCipher
import dagger.hilt.android.qualifiers.ApplicationContext
import java.security.GeneralSecurityException
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext




//暗号文の保存。
private val Context.authSessionDataStore by preferencesDataStore(
    name = "auth_session"
)

class TokenRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {



    private val encryptedTokenKey =
        stringPreferencesKey("encrypted_access_token")

    private val cipher by lazy {//TokenCipher を利用する。lazy=必要になったら、cipher生成

        TokenCipher()

    }



    suspend fun saveToken(token: String) {
        val encrypted = withContext(Dispatchers.IO) {//ブロッキングする可能性がある処理を、UIスレッドと別のIO用スレッドで実行
            cipher.encrypt(token)//暗号化する(Ktorから受け取ったトークン）
        }

        context.authSessionDataStore.edit { preferences ->
            preferences[encryptedTokenKey] = encrypted//暗号文を保存する
        }
    }




    suspend fun getToken(): String? {
        val encrypted = context.authSessionDataStore.data//取得
            .first()[encryptedTokenKey] ?: return null

        return try {
            withContext(Dispatchers.IO) {
                cipher.decrypt(encrypted)//復号化→取得し、Ktor側で検証させる
            }
        } catch (e: GeneralSecurityException) {
            clearToken()
            null
        } catch (e: IllegalArgumentException) {
            clearToken()
            null
        } catch (e: IllegalStateException) {
            clearToken()
            null
        }
    }

    suspend fun clearToken() {//トークンの削除（ログアウト時などに利用）
        context.authSessionDataStore.edit { preferences ->
            preferences.remove(encryptedTokenKey)
        }
    }
}

//RegisterScreen
//    ↓
//RegisterViewModel
//    ↓
//NetworkAuthRepository.register()
//    ↓
//AuthApiService（Retrofit）
//    ↓
//Ktorでユーザー登録
//    ↓
//トークンを受信
//    ↓
//TokenRepository.saveToken()
//    ↓
//TokenCipher.encrypt()
//    ↓
//DataStoreに保存
//    ↓
//RegisterUiState.Success
//    ↓
//登録完了画面
