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
        )//StateFlowはFlowと比べ、常に現在の値を１つ保持している。

    val uiState: StateFlow<MainUiState> =
        _uiState.asStateFlow()//外部用。


    private var loadMatchesJob: Job? = null
    private var loadingCompetitionCode: String? = null

    private var observeMatchesJob: Job? = null

    private var observedCompetitionCode: String? = null

    init {//LaunchedEffectは、変化するkeyを使う。（双方とも記述された関数内のものを動かす）。initは1回通信する。
        loadData()
    }
    //LaunchedEffectはComposableがCompositionに入ったときに処理される。initは、記述されているクラスのインスタンスが生成される際に実行
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
            return//同じリーグタップならその処理終了。
        }


        loadMatchesJob?.cancel()//リーグが違うなら以前のコルーチンをキャンセル

        loadingCompetitionCode = competitionCode



        if (observedCompetitionCode != competitionCode) {//二度タップしたらコルーチンキャンセル、別リーグなら以前のコルーチンを止める。
            observeMatchesJob?.cancel()

            observedCompetitionCode = competitionCode

            observeMatchesJob = viewModelScope.launch {//リーグ選択時に1回関数動く。Flow作る。
                repository.observeMatches(competitionCode)//collectがroom内のテーブル変更を感知
                    .collect { matches ->

                        val state =
                            _uiState.value as? MainUiState.Success
                                ?: return@collect

                        if (loadingCompetitionCode == competitionCode) {
                            _uiState.value = state.copy(
                                matches = matches
                            )
                        }
                    }
            }
        }

        loadMatchesJob =  viewModelScope.launch {//Dispatcherを指定していない＝ViewmodelScopeの設定を引き継ぐ
            val currentState = _uiState.value

            if (currentState !is MainUiState.Success) {
                return@launch
            }

            val matches = repository.getMatches(
                competitionCode
            )//ここでmatchリスト取得


            // 取得中に別リーグへ切り替わっていたら、処理終了。
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

//最初にリーグをタップ(Flow仕組み）
//
//LeagueListScreen
//  .clickable
//       ↓
//AppNavigation
//  onLeagueSelected(leagueId)
//       ↓
//MainRouteから渡された
//  mainViewModel.loadMatches(leagueId)
//       ↓
//MainViewModel
//  loadMatches()
//       │
//       ├── repository.observeMatches("PL")
//       │         ↓
//       │    MatchRepository.observeMatches()
//       │         ↓
//       │    MatchDao.observeMatchesByLeague()
//       │         ↓
//       │    RoomがFlowを提供
//       │         ↓
//       │    collect開始
//       │         ↓
//       │    Roomが最初の試合一覧を返す
//       │
//       └── repository.getMatches("PL")
//                 ↓
//            RetrofitでAPI取得
//                 ↓
//            Roomに試合一覧を保存
//                 ↓
//            Roomが変更を検知
//                 ↓
//            SQLを再実行
//                 ↓
//            mapでMatchに変換
//                 ↓
//            MainViewModelのcollectへ
//                 ↓
//            _uiState.value更新
//
//
//その後、試合詳細で更新ボタンを押す
//
//MatchThreadScreen
//  onRefresh()
//       ↓
//MatchDetailViewModel
//  refreshMatch()
//       ↓
//MatchRepository
//  refreshMatchById()
//       ↓
//Retrofit
//  最新の試合情報を取得
//       ↓
//MatchDao
//  upsertMatches()
//       ↓
//Roomのmatchesテーブル変更
//       ↓
//RoomがSQLを再実行
//       ↓
//Repositoryのmap再実行
//       ↓
//MainViewModelのcollect再実行
//       ↓
//_uiState.value更新
//       ↓
//MainRouteが新しいuiStateを受信
//       ↓
//MatchListScreenの表示が更新


