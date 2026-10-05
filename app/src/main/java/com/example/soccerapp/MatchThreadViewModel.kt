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
import android.util.Log
import androidx.lifecycle.SavedStateHandle

@HiltViewModel
class MatchThreadViewModel @Inject constructor(
    private val repository: CommentRepository,
    private val savedStateHandle: SavedStateHandle//ViewModel消滅後の小さいUI状態復元に使える
) : ViewModel(){

    private val matchId: Int =
        checkNotNull(savedStateHandle["matchId"])

    init {
        loadComments()
    }



    private val  _uiState = MutableStateFlow<MatchThreadUiState>(
        MatchThreadUiState.Loading
    )

    val uiState = _uiState.asStateFlow()//外部


    fun loadComments() {//コメ欄ゲット
        viewModelScope.launch {


            val hasComments =
                _uiState.value is MatchThreadUiState.Success

            if (!hasComments) {
                _uiState.value = MatchThreadUiState.Loading
            }


            try {
                val comments = repository.getComments(matchId)

                val currentState =
                    _uiState.value as? MatchThreadUiState.Success


                _uiState.value =
                    if (currentState != null) {
                        currentState.copy(
                            comments = comments//2回目の取得の際に既存コメントを更新して消さないため。(copy)手動だとほかの値が初期化。
                        )

                    } else {
                        MatchThreadUiState.Success(
                            comments = comments
                        )
                    }


            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.value = MatchThreadUiState.Error(
                    message = e.message ?: "Failed to load comments"
                )
            }
        }
    }


    fun onAuthorChange(newAuthor: String) {
        val currentState = _uiState.value as? MatchThreadUiState.Success
            ?: return

        _uiState.value = currentState.copy(
            author = newAuthor
        )
    }


    fun onTextChange(newText: String) {//コメント投稿中の状態保持
        val currentState = _uiState.value as? MatchThreadUiState.Success
            ?: return



        _uiState.value = currentState.copy(text = newText)
    }




    fun postComment(//コメント投稿用
        author: String,
        text: String
    ) {
        viewModelScope.launch {
            val currentState =
                _uiState.value as? MatchThreadUiState.Success
                    ?: return@launch


            if (currentState.isPosting) {
                return@launch
            }


            _uiState.value = currentState.copy(//コメント登校中UI用(投稿ボタン連打禁止)
                isPosting = true,
                postErrorMessage = null
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
                    isPosting = false,//サーバー通信成功。false→このファイル内でisPostingの状態を管理
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