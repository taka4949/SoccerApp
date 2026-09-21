package com.example.soccerapp


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.soccerapp.data.repository.CommentRepository
import com.example.soccerapp.ui.state.MatchThreadUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class MatchThreadViewModel @Inject constructor(
    private val repository: CommentRepository
) : ViewModel(){


    private val  _uiState = MutableStateFlow<MatchThreadUiState>(
        MatchThreadUiState.Loading
    )

    val uiState = _uiState.asStateFlow()//外部


    fun loadComments(matchId: Int) {//コメ欄ゲット
        viewModelScope.launch {
            _uiState.value = MatchThreadUiState.Loading

            try {
                val comments = repository.getComments(matchId)

                _uiState.value = MatchThreadUiState.Success(
                    comments = comments
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.value = MatchThreadUiState.Error(
                    message = e.message ?: "Failed to load comments"
                )
            }
        }
    }


    fun postComment(//コメント投稿用
        matchId: Int,
        author: String,
        text: String
    ) {
        viewModelScope.launch {
            val currentState =
                _uiState.value as? MatchThreadUiState.Success
                    ?: return@launch//post時はコメ欄在り→成功状態のはずだが、それ以外なら関数終了（保険）

            _uiState.value = currentState.copy(//コメント登校中UI用(trueにすることで投稿ボタン連打禁止
                isPosting = true,
                postErrorMessage = null//保険（エラー→再ポスト時にnullに更新できる）
            )

            try {
                val comment = repository.createComment(
                    matchId = matchId,
                    author = author,
                    text = text
                )

                _uiState.value = currentState.copy(//既存コメ欄表示するためにcopy必須
                    comments = currentState.comments + comment,
                    author = author,
                    text = "",
                    isPosting = false,//サーバー通信成功だからfalse→ここでする理由は、このファイル内でisPostingの状態を管理
                    postErrorMessage = null
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.value = currentState.copy(
                    isPosting = false,//失敗だから、もう一度送信ボダン復活。
                    postErrorMessage = e.message ?: "Failed to post comment"
                )
            }
        }
    }

}


//isPostingは、このファイル内で完結させる。ポスト中にtrue、ポスト成功時にfalse→二重投稿防止
//copyにより、既存uiを表示したまま新たなuiを表示できる。