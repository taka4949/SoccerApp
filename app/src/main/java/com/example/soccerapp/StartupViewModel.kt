package com.example.soccerapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.soccerapp.data.repository.NetworkAuthRepository
import com.example.soccerapp.data.repository.SessionVerificationResult
import com.example.soccerapp.data.repository.UserPreferencesRepository
import com.example.soccerapp.ui.state.SessionUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.CancellationException


@HiltViewModel
class StartupViewModel @Inject constructor(
    private val preferencesRepository: UserPreferencesRepository,
    private val authRepository: NetworkAuthRepository//hilt
) : ViewModel() {

    val showAccountPrompt: StateFlow<Boolean?> =
        preferencesRepository.hasSeenAccountPrompt
            .map<Boolean, Boolean?> { hasSeen -> !hasSeen }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(),
                initialValue = null//初期画面（これを保持するにはstate型にする必要ある）
            )
                   //初回false→trueへ→RouteのshowPrompt=true→アカウント登録案内表示


    fun dismissAccountPrompt() {//初回登録画面の表示を以後スキップ
        viewModelScope.launch {
            preferencesRepository.markPromptAsSeen()//trueへ変更→このファイルではfalseへ変更
        }
    }




    private val _sessionUiState =
        MutableStateFlow<SessionUiState>(SessionUiState.Loading)



    val sessionUiState: StateFlow<SessionUiState> =
        _sessionUiState.asStateFlow()

    private var sessionCheckJob: Job? = null

    init {
        refreshSession()
    }

    fun refreshSession() {


        sessionCheckJob?.cancel()

        sessionCheckJob = viewModelScope.launch {
            _sessionUiState.value = SessionUiState.Loading

            try {
                val result = authRepository.verifySession()//認証開始

                _sessionUiState.value = when (result) {//結果をUIに流す。
                    is SessionVerificationResult.LoggedIn ->
                        SessionUiState.LoggedIn(result.userId)

                    SessionVerificationResult.LoggedOut ->
                        SessionUiState.LoggedOut

                    SessionVerificationResult.Unavailable ->
                        SessionUiState.Unavailable
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _sessionUiState.value = SessionUiState.Unavailable
            }
        }
    }

    fun logout() {
        sessionCheckJob?.cancel()
        viewModelScope.launch {
            _sessionUiState.value = SessionUiState.Loading
            try {
                authRepository.logout()
                _sessionUiState.value = SessionUiState.LoggedOut
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _sessionUiState.value = SessionUiState.Unavailable
            }
        }
    }
}

//RouteでViewModel生成
//　　↓
//showAccountPrompt = null
//（判定前）
//    ↓
//DataStoreを読み取る
//    ↓
//保存キーがまだない
//    ↓
//Repositoryがfalseを返す
//（以前に案内を見ていない）
//    ↓
//mapで!false → true
//    ↓
//showAccountPrompt = true
//    ↓
//登録案内を表示
//    ↓
//ユーザーが「スキップ」を押す
//    ↓
//DataStoreに表示済みのtrueを保存
//    ↓
//Repositoryからtrueが届く
//    ↓
//mapで!true → false
//    ↓
//showAccountPrompt = false
//    ↓
//通常画面を表示