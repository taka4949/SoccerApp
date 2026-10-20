
package com.example.soccerapp.data.repository

import com.example.soccerapp.data.remote.api.AuthApiService
import com.example.soccerapp.data.remote.dto.RegisterRequestDto
import com.example.soccerapp.data.remote.dto.LoginRequestDto
import java.io.IOException
import javax.inject.Inject





//これは、1回の登録処理がどんな結果で終わったのかを表す。
//一方、UiStateは、今、画面をどんな状態で表示するのかを表す。

sealed interface RegistrationResult {

    data class Success(
        val id: Long,
        val email: String
    ) : RegistrationResult

    object InvalidInput : RegistrationResult

    object EmailAlreadyRegistered : RegistrationResult
}

sealed interface LoginResult {
    data class Success(val userId: Long) : LoginResult
    data object InvalidCredentials : LoginResult
}

sealed interface SessionVerificationResult {//認証確認の結果を表す
    data class LoggedIn(
        val userId: Long
    ) : SessionVerificationResult

    data object LoggedOut : SessionVerificationResult

    data object Unavailable : SessionVerificationResult
}





class NetworkAuthRepository @Inject constructor(
    private val authApiService: AuthApiService,
    private val tokenRepository: TokenRepository
) {

    suspend fun register(//引数受け取って、APIから送る
        email: String,
        password: String
    ): RegistrationResult {

        val request = RegisterRequestDto(
            email = email,
            password = password
        )



        val response = authApiService.register(request)
        //HTTPステータスと本文の両方を扱えるRetrofitのResponseオブジェクト

        return when (response.code()) {
            //HTTPのステータス番号をViewModelへ持ち込まない

            201 -> {
                val user = response.body()
                    ?: throw IllegalStateException(
                        "Registration response body is missing"
                    )

                tokenRepository.saveToken(user.accessToken)//トークンを暗号化→保存


                RegistrationResult.Success(
                    id = user.id,
                    email = user.email
                )
            }



            400 -> RegistrationResult.InvalidInput

            409 -> RegistrationResult.EmailAlreadyRegistered

            else -> throw IOException(
                "Registration failed: HTTP ${response.code()}"
            )
        }
    }

    //ログイン成功時は登録時と同じTokenRepositoryを利用する
    suspend fun login(email: String, password: String): LoginResult {
        val response = authApiService.login(
            LoginRequestDto(email = email, password = password)
        )
        return when (response.code()) {
            200 -> {
                val session = response.body()
                    ?: throw IllegalStateException("Login response body is missing")
                tokenRepository.saveToken(session.accessToken)
                LoginResult.Success(session.userId)
            }

            400, 401 -> LoginResult.InvalidCredentials

            else -> throw IOException(
                "Login failed: HTTP ${response.code()}"
            )
        }
    }

    //サーバーのセッションを無効化し、端末の保存情報を削除する
    suspend fun logout() {
        val token = tokenRepository.getToken()
        try {
            if (token != null) {
                authApiService.logout("Bearer $token")
            }
        } catch (_: IOException) {
            //通信できない場合でも端末側のログイン状態は解除する
        } finally {
            tokenRepository.clearToken()
        }
    }

    suspend fun verifySession(): SessionVerificationResult {
        val token = tokenRepository.getToken()
            ?: return SessionVerificationResult.LoggedOut

        return try {
            val response = authApiService.getCurrentUser(
                "Bearer $token"
            )

            when (response.code()) {
                200 -> {
                    val user = response.body()
                    if (user != null) {
                        SessionVerificationResult.LoggedIn(user.userId)
                    } else {
                        SessionVerificationResult.Unavailable
                    }
                }

                401 -> {
                    tokenRepository.clearToken()
                    SessionVerificationResult.LoggedOut
                }

                else -> SessionVerificationResult.Unavailable
            }
        } catch (e: IOException) {
            SessionVerificationResult.Unavailable
        }
    }
}

//SoccerAppを起動
//      ↓
//TokenRepository.getToken()
//      ↓
//保存済みトークンがある？
//      │
//      ├─ ない → 未ログイン
//      │
//      └─ ある
//           ↓
//       GET /auth/me
//           ↓
//       Ktorがトークンを検証
//           │
//           ├─ 200 → ログイン済み
//           ├─ 401 → 未ログイン
//           └─ 通信失敗 → 確認できない
