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

@OptIn(ExperimentalCoroutinesApi::class)
class MatchThreadViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

}

