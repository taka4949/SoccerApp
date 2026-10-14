package com.example.soccerapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.soccerapp.data.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


@HiltViewModel
class StartupViewModel @Inject constructor(
    private val preferencesRepository: UserPreferencesRepository
) : ViewModel() {

    val showAccountPrompt: StateFlow<Boolean?> =
        preferencesRepository.hasSeenAccountPrompt
            .map<Boolean, Boolean?> { hasSeen -> !hasSeen }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(),
                initialValue = null//初期画面（これを保持するにはstate型にする必要ある）
            )
    //初回false→trueへ→RouteのshowPrompt=trueへ


    fun dismissAccountPrompt() {
        viewModelScope.launch {
            preferencesRepository.markPromptAsSeen()//trueへ変更→このファイルではfalseへ変更
        }
    }

}

//アプリアイコンをタップ
//    ↓
//MainActivityが起動
//    ↓
//StartupViewModelが作られる
//    ↓
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