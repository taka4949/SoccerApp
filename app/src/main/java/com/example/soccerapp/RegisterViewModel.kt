
package com.example.soccerapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.soccerapp.data.repository.NetworkAuthRepository
import com.example.soccerapp.data.repository.RegistrationResult
import com.example.soccerapp.ui.state.RegisterUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch




@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val repository: NetworkAuthRepository
) : ViewModel() {

    private val _uiState =
        MutableStateFlow<RegisterUiState>(
            RegisterUiState.Idle
        )


    val uiState: StateFlow<RegisterUiState> =
        _uiState.asStateFlow()



    fun register(
        email: String,
        password: String
    ) {


        if (_uiState.value is RegisterUiState.Loading) {//二重タップ防止
            return
        }

        _uiState.value = RegisterUiState.Loading

        viewModelScope.launch {
            try {
                val result = repository.register(//アカウント登録
                    email = email,
                    password = password
                )

                _uiState.value = when (result) {
                    is RegistrationResult.Success -> {
                        RegisterUiState.Success(
                            id = result.id,
                            email = result.email
                        )
                    }

                    RegistrationResult.InvalidInput -> {
                        RegisterUiState.Error(
                            "入力内容を確認してください"
                        )
                    }

                    RegistrationResult.EmailAlreadyRegistered -> {
                        RegisterUiState.Error(
                            "このメールアドレスは登録済みです"
                        )
                    }
                }

            } catch (e: CancellationException) {
                throw e

            } catch (e: Exception) {
                _uiState.value = RegisterUiState.Error(////Retrofitの通信自体が失敗して例外になる場合
                    "通信またはサーバー処理に失敗しました"
                )
            }
        }
    }
}
