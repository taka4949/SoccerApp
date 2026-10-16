
package com.example.soccerapp.data.repository

import com.example.soccerapp.data.remote.api.AuthApiService
import com.example.soccerapp.data.remote.dto.RegisterRequestDto
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





class NetworkAuthRepository @Inject constructor(
    private val authApiService: AuthApiService
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
}

