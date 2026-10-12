package com.example.soccerapp

import androidx.lifecycle.SavedStateHandle
import com.example.soccerapp.data.model.Comment
import com.example.soccerapp.data.repository.CommentRepository
import com.example.soccerapp.ui.state.MatchThreadUiState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.test.runCurrent


@OptIn(ExperimentalCoroutinesApi::class)
class MatchThreadViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun loadComments_success() = runTest {//runTest=Coroutine用のテスト環境整備

        val repository = FakeCommentRepository()

        val savedStateHandle = SavedStateHandle(
            mapOf("matchId" to 1)
        )

        val viewModel = MatchThreadViewModel(
            repository,
            savedStateHandle
        )//MatchThreadViewmodelはFakeを利用。

        advanceUntilIdle()//Coroutineの非同期処理をスキップ

        val state = viewModel.uiState.value as MatchThreadUiState.Success//StateFlowから現在値を取り出す

        assertEquals(1, state.comments.size)

        assertEquals("Hello", state.comments[0].text)
    }




    @Test
    fun postComment_success() = runTest {

        val repository = FakeCommentRepository()

        val savedStateHandle = SavedStateHandle(
            mapOf("matchId" to 1)
        )

        val viewModel = MatchThreadViewModel(
            repository,
            savedStateHandle
        )

        advanceUntilIdle()


        viewModel.onTextChange("Second")

        val inputState =
            viewModel.uiState.value as MatchThreadUiState.Success

        assertEquals("Second", inputState.text)



        viewModel.postComment(
            author = "TABATA",
            text = "Second"
        )

        advanceUntilIdle()

        val state =
            viewModel.uiState.value as MatchThreadUiState.Success

        assertEquals(2, state.comments.size)

        assertEquals("Second", state.comments[1].text)

        assertEquals("", state.text)

        assertEquals(false, state.isPosting)
    }




    @Test
    fun postComment_failure() = runTest {
        val repository =
            FakeCommentRepository(shouldFailPost = true)

        val savedStateHandle = SavedStateHandle(
            mapOf("matchId" to 1)
        )

        val viewModel = MatchThreadViewModel(
            repository,
            savedStateHandle
        )

        advanceUntilIdle()


        viewModel.onAuthorChange("TABATA")
        viewModel.onTextChange("Second")//投稿失敗時にコメント残るか？

        viewModel.postComment(
            author = "TABATA",
            text = "Second"
        )
        advanceUntilIdle()

        val state =
            viewModel.uiState.value as MatchThreadUiState.Success

        assertEquals(1, state.comments.size)//Hello
        assertEquals("Second", state.text)
        assertEquals(false, state.isPosting)
        assertEquals("Post failed", state.postErrorMessage)
    }


    @Test
    fun inputChange_updatesState() = runTest {
        val repository = FakeCommentRepository()

        val savedStateHandle = SavedStateHandle(
            mapOf("matchId" to 1)
        )

        val viewModel = MatchThreadViewModel(
            repository,
            savedStateHandle
        )

        advanceUntilIdle()

        viewModel.onAuthorChange("TABATA")
        viewModel.onTextChange("Second")

        val state =
            viewModel.uiState.value as MatchThreadUiState.Success

        assertEquals("TABATA", state.author)
        assertEquals("Second", state.text)
    }



    @Test
    fun postComment_retry_success() = runTest {
        val repository =
            FakeCommentRepository(shouldFailPost = true)

        val savedStateHandle = SavedStateHandle(
            mapOf("matchId" to 1)
        )

        val viewModel = MatchThreadViewModel(
            repository,
            savedStateHandle
        )

        advanceUntilIdle()

        viewModel.onAuthorChange("TABATA")
        viewModel.onTextChange("Second")

        // 1回目は失敗
        viewModel.postComment(
            author = "TABATA",
            text = "Second"
        )
        advanceUntilIdle()

        // 次は成功するFakeに切り替える
        repository.shouldFailPost = false

        // 2回目は成功
        viewModel.postComment(
            author = "TABATA",
            text = "Second"
        )
        advanceUntilIdle()

        val state =
            viewModel.uiState.value as MatchThreadUiState.Success

        assertEquals(2, state.comments.size)
        assertEquals("Second", state.comments[1].text)
        assertEquals("", state.text)
        assertEquals(false, state.isPosting)
        assertEquals(null, state.postErrorMessage)
    }


    @Test
    fun postComment_whilePosting_isPostingTrue() = runTest {
        val repository =
            FakeCommentRepository(shouldSuspendPost = true)

        val savedStateHandle = SavedStateHandle(
            mapOf("matchId" to 1)
        )

        val viewModel = MatchThreadViewModel(
            repository,
            savedStateHandle
        )

        advanceUntilIdle()

        viewModel.onAuthorChange("TABATA")
        viewModel.onTextChange("Second")

        viewModel.postComment(
            author = "TABATA",
            text = "Second"
        )

        runCurrent()//途中状態を見ることに向いている。

        val postingState =
            viewModel.uiState.value as MatchThreadUiState.Success

        assertEquals(true, postingState.isPosting)

        repository.allowPostToComplete.complete(Unit)

        advanceUntilIdle()

        val finishedState =
            viewModel.uiState.value as MatchThreadUiState.Success

        assertEquals(false, finishedState.isPosting)
    }




    @Test
    fun postComment_whilePosting_doesNotPostTwice() = runTest {
        val repository =
            FakeCommentRepository(shouldSuspendPost = true)

        val savedStateHandle = SavedStateHandle(
            mapOf("matchId" to 1)
        )

        val viewModel = MatchThreadViewModel(
            repository,
            savedStateHandle
        )


        advanceUntilIdle()


        // 1回目
        viewModel.postComment(
            author = "TABATA",
            text = "Second"
        )

        runCurrent()

        // まだ1回目は await() で止まっている
        // その間に2回目
        viewModel.postComment(
            author = "TABATA",
            text = "Second"
        )

        runCurrent()

        assertEquals(1, repository.createCommentCallCount)

        repository.allowPostToComplete.complete(Unit)
        advanceUntilIdle()
    }


@Test
fun postComment_threadChanged_replacesComments() = runTest {
    val repository = FakeCommentRepository()

    val viewModel = MatchThreadViewModel(
        repository,
        SavedStateHandle(mapOf("matchId" to 1))
    )//init

    advanceUntilIdle()


    repository.switchThread = true


    viewModel.postComment("TABATA", "Second")
    advanceUntilIdle()

    val state =
        viewModel.uiState.value as MatchThreadUiState.Success

    assertEquals(1, state.comments.size)
    assertEquals(11L, state.comments[0].threadId)
    assertEquals("Second", state.comments[0].text)
    assertEquals(2, repository.getCommentsCallCount)//getComments関数が2回かどうか
}
}




private class FakeCommentRepository(
    var shouldFailPost: Boolean = false,
    private val shouldSuspendPost: Boolean = false
) : CommentRepository {

    val postStarted = CompletableDeferred<Unit>()
    val allowPostToComplete = CompletableDeferred<Unit>()

    var createCommentCallCount = 0

    var switchThread = false
    var getCommentsCallCount = 0

    private var switchedComment: Comment? = null

    override suspend fun getComments(
        matchId: Int
    ): List<Comment> {

        getCommentsCallCount++


        val comment = switchedComment
        if (comment != null) {
            return listOf(comment)
        }



        return listOf(
            Comment(
                id = 1L,
                matchId = matchId,
                author = "TABATA",
                text = "Hello",
                threadId = if (switchThread) 11L else 10L,
                createdAt = "2026-10-03"
            )
        )
    }

    override suspend fun createComment(
        matchId: Int,
        author: String,
        text: String
    ): Comment {

        createCommentCallCount++


        if (shouldFailPost) {
            throw Exception("Post failed")
        }

        if (shouldSuspendPost) {
            allowPostToComplete.await()
        }

        val comment = Comment(
            id = 2L,
            matchId = matchId,
            threadId = if (switchThread) 11L else 10L,
            author = author,
            text = text,
            createdAt = "2026-10-03"
        )


        if (switchThread) {
            switchedComment = comment
        }

        return comment

    }
}