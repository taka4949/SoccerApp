package com.example.soccerapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.soccerapp.data.repository.LoginResult
import com.example.soccerapp.data.repository.NetworkAuthRepository
import com.example.soccerapp.ui.state.LoginUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: NetworkAuthRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun login(email: String, password: String) {
        if (_uiState.value is LoginUiState.Loading) return

        _uiState.value = LoginUiState.Loading
        viewModelScope.launch {
            try {
                _uiState.value = when (repository.login(email, password)) {
                    is LoginResult.Success -> LoginUiState.Success
                    LoginResult.InvalidCredentials ->
                        LoginUiState.Error("メールアドレスまたはパスワードが正しくありません")
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.value = LoginUiState.Error("通信またはサーバー処理に失敗しました")
            }
        }
    }
}
