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
import kotlinx.coroutines.Job



@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository : SoccerRepository,
) : ViewModel(){//viewmodelScopeやライフサイクル管理、再描画時の画面保持の継承を継承している（クラス）



    private val _uiState =
        MutableStateFlow<MainUiState>(
            MainUiState.Loading
        )

    val uiState: StateFlow<MainUiState> =
        _uiState.asStateFlow()//外部用。


    private var loadMatchesJob: Job? = null
    private var loadingCompetitionCode: String? = null

    init {//LaunchedEffectとの違い重要。こっちは変化ないものを使う。再コンポーズでは通信を1回で済ませられる。
        loadData()
    }
    fun loadData() {
         viewModelScope.launch {//scope＝寿命が必要な理由は、無駄な更新の負担を無くすため。launchがコルーチンの状態を管理している。
            _uiState.value =
                MainUiState.Loading//初期状態のため。

            try {//はLoadingが画面に描かれるまで待たない！。
                val leagues =
                    repository.getLeagues()

                _uiState.value =
                    MainUiState.Success(
                        leagues = leagues,
                        matches = emptyList()
                    )
            } catch (exception: CancellationException) {
                throw exception
            } catch (e: Exception) {
                _uiState.value =
                    MainUiState.Error(
                        message =
                            e.message
                                ?: "Unknown error"
                    )
            }
        }
    }
    fun loadMatches(
        competitionCode: String
    ) {

        if (
            loadMatchesJob?.isActive == true &&
            loadingCompetitionCode == competitionCode
        ) {
            return
        }


        loadMatchesJob?.cancel()

        loadingCompetitionCode = competitionCode

        loadMatchesJob =  viewModelScope.launch {//Dispatcherを指定していない＝ViewmodelScopeの設定を引き継ぐ
            val currentState = _uiState.value

            if (currentState !is MainUiState.Success) {
                return@launch
            }

            val matches = repository.getMatches(
                competitionCode
            )//ここでmatchリスト取得


            // 取得中に別リーグへ切り替わっていたら、
            // 古い結果で画面を上書きしない
            if (loadingCompetitionCode != competitionCode) {
                return@launch
            }



            _uiState.value = currentState.copy(//MainScreenのMainRouteがcollectする。
                matches = matches
            )
        }
    }
}



