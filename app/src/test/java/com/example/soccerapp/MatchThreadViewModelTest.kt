package com.example.soccerapp

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

        val viewModel = MatchThreadViewModel(repository)//MatchThreadViewmodelはFakeを利用。

        viewModel.loadInitialComments(1)
        advanceUntilIdle()//Coroutineの非同期処理をスキップ

        val state = viewModel.uiState.value as MatchThreadUiState.Success//StateFlowから現在値を取り出す

        assertEquals(1, state.comments.size)

        assertEquals("Hello", state.comments[0].text)
    }


    @Test
    fun loadInitialComments_sameMatch_doesNotReloadOrClearDraft() = runTest {
        val repository = FakeCommentRepository()
        val viewModel = MatchThreadViewModel(repository)

        viewModel.loadInitialComments(1)
        advanceUntilIdle()

        viewModel.onAuthorChange("TABATA")
        viewModel.onTextChange("Second")

        viewModel.loadInitialComments(1)
        advanceUntilIdle()

        val state = viewModel.uiState.value as MatchThreadUiState.Success

        assertEquals(1, repository.getCommentsCallCount)
        assertEquals("TABATA", state.author)
        assertEquals("Second", state.text)
    }


    @Test
    fun retryComments_runsRequestAgain() = runTest {
        val repository = FakeCommentRepository()
        val viewModel = MatchThreadViewModel(repository)

        viewModel.loadInitialComments(1)
        advanceUntilIdle()

        viewModel.retryComments(1)
        advanceUntilIdle()

        assertEquals(2, repository.getCommentsCallCount)
    }




    @Test
    fun postComment_success() = runTest {

        val repository = FakeCommentRepository()
        val viewModel = MatchThreadViewModel(repository)

        viewModel.loadInitialComments(1)
        advanceUntilIdle()


        viewModel.onTextChange("Second")

        val inputState =
            viewModel.uiState.value as MatchThreadUiState.Success

        assertEquals("Second", inputState.text)



        viewModel.postComment(
            matchId = 1,
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

    val viewModel = MatchThreadViewModel(repository)

    viewModel.loadInitialComments(1)
    advanceUntilIdle()


    viewModel.onAuthorChange("TABATA")
    viewModel.onTextChange("Second")//投稿失敗時にコメント残るか？

    viewModel.postComment(
        matchId = 1,
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
        val viewModel = MatchThreadViewModel(repository)

        viewModel.loadInitialComments(1)
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

        val viewModel = MatchThreadViewModel(repository)

        viewModel.loadInitialComments(1)
        advanceUntilIdle()

        viewModel.onAuthorChange("TABATA")
        viewModel.onTextChange("Second")

        // 1回目は失敗
        viewModel.postComment(
            matchId = 1,
            author = "TABATA",
            text = "Second"
        )
        advanceUntilIdle()

        // 次は成功するFakeに切り替える
        repository.shouldFailPost = false

        // 2回目は成功
        viewModel.postComment(
            matchId = 1,
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

        val viewModel = MatchThreadViewModel(repository)

        viewModel.loadInitialComments(1)
        advanceUntilIdle()

        viewModel.onAuthorChange("TABATA")
        viewModel.onTextChange("Second")

        viewModel.postComment(
            matchId = 1,
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

        val viewModel = MatchThreadViewModel(repository)

        viewModel.loadInitialComments(1)
        advanceUntilIdle()

        viewModel.onAuthorChange("TABATA")
        viewModel.onTextChange("Second")

        // 1回目
        viewModel.postComment(
            matchId = 1,
            author = "TABATA",
            text = "Second"
        )

        runCurrent()

        // まだ1回目は await() で止まっている
        // その間に2回目
        viewModel.postComment(
            matchId = 1,
            author = "TABATA",
            text = "Second"
        )

        runCurrent()

        assertEquals(1, repository.createCommentCallCount)

        repository.allowPostToComplete.complete(Unit)
        advanceUntilIdle()
    }
}



private class FakeCommentRepository(
    var shouldFailPost: Boolean = false,
    private val shouldSuspendPost: Boolean = false
) : CommentRepository {

    val postStarted = CompletableDeferred<Unit>()
    val allowPostToComplete = CompletableDeferred<Unit>()

    var createCommentCallCount = 0
    var getCommentsCallCount = 0

    override suspend fun getComments(
        matchId: Int
    ): List<Comment> {
        getCommentsCallCount++

        return listOf(
            Comment(
                id = 1L,
                matchId = matchId,
                author = "TABATA",
                text = "Hello",
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

        return Comment(
            id = 2L,
            matchId = matchId,
            author = author,
            text = text,
            createdAt = "2026-10-03"
        )
    }



}