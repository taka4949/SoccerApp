package com.example.soccerapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.soccerapp.data.repository.SoccerRepository
import com.example.soccerapp.ui.state.MainUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
@HiltViewModel
class MainViewModel @Inject constructor(//自分でリポジトリは書かない。外から受け取る（hilt)
    private val repository : SoccerRepository//ここはインターフェース
) : ViewModel(){


    // 「内部書き換え用（Mutable）」と「外部公開用（読み取り専用）
    //２つに分ける理由は、UI側からleaguesなどのデータを変更させないため。バグ防止。
    private val _uiState =
        MutableStateFlow<MainUiState>(
            MainUiState.Loading
        )

    val uiState: StateFlow<MainUiState> =
        _uiState.asStateFlow()//外部に公開用、ここはデータの更新を直接行わない、使わない。

    init {
        loadData()
    }

    fun loadData() {//mainから通常関数を呼ぶ。（無駄なコードの減少）
        viewModelScope.launch {//scope＝寿命が必要な理由は、無駄な更新の負担を無くすため。launchがコルーチンの状態を管理している。
            _uiState.value =
                MainUiState.Loading//初期状態のため。

            try {
                val leagues =
                    repository.getLeagues()

                _uiState.value =
                    MainUiState.Success(
                        leagues = leagues,
                        matches = emptyList()
                    )
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                _uiState.value =
                    MainUiState.Error(
                        message =
                            exception.message
                                ?: "Unknown error"
                    )
            }
        }
    }
    fun loadMatches(
        competitionCode: String
    ) {
        viewModelScope.launch {
            val currentState = _uiState.value

            if (currentState !is MainUiState.Success) {
                return@launch
            }

            val matches = repository.getMatches(
                competitionCode
            )

            _uiState.value = currentState.copy(
                matches = matches
            )//リーグ表示のuiを既存のままで、マッチ情報のみ更新する。
        }//変数currentstateを持つ理由↓
    }
}

//uiState.value
//= データ
//= ただし型は MainUiState として見えている
//
//currentState
//= 同じデータを受け取ったもの
//= is Success の確認後は Success 型として見える