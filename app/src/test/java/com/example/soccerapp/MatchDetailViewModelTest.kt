package com.example.soccerapp


import com.example.soccerapp.data.model.League
import com.example.soccerapp.data.model.Match
import com.example.soccerapp.data.repository.SoccerRepository
import com.example.soccerapp.ui.state.MatchDetailUiState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)

class MatchDetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()






    @Test
    fun  loadMatch_success() = runTest{


        val repository = FakeSoccerRepository()

        val viewModel = MatchDetailViewModel(repository)

        viewModel.loadMatch(1)

        advanceUntilIdle()

        val state = viewModel.uiState.value as MatchDetailUiState.Success

        assertEquals(1,state.match.id)
        assertEquals("Home",state.match.homeTeam)
    }


    @Test
    fun loadMatch_notFound() = runTest {

        val repository =
            FakeSoccerRepository(
                shouldReturnNull = true
            )

        val viewModel =
            MatchDetailViewModel(repository)

        viewModel.loadMatch(999)

        advanceUntilIdle()

        assertEquals(
            MatchDetailUiState.NotFound,
            viewModel.uiState.value
        )
    }


    @Test
    fun loadMatch_failure() = runTest {

        val repository =
            FakeSoccerRepository(
                shouldFail = true
            )

        val viewModel =
            MatchDetailViewModel(repository)

        viewModel.loadMatch(1)

        advanceUntilIdle()

        val state =
            viewModel.uiState.value as MatchDetailUiState.Error

        assertEquals(
            "Load failed",
            state.message
        )
    }



    private class FakeSoccerRepository(
        private val shouldReturnNull: Boolean = false,
        private val shouldFail: Boolean = false
    ): SoccerRepository{

        override suspend fun  getLeagues(): List<League> {
            return emptyList()
        }

        override suspend fun getMatches(
            competitionCode: String
        ): List<Match> {
            return emptyList()
        }

        override suspend fun getMatchById(
            matchId: Int
        ): Match? {



            if (shouldFail) {
                throw Exception("Load failed")
            }


            if (shouldReturnNull) {
                return null
            }


            return Match(
                id = matchId,
                leagueId = "PL",
                homeTeam = "Home",
                awayTeam = "Away",
                homeScore = null,
                awayScore = null,
                utcDate = "2026-10-04T12:00:00Z",
                status = "SCHEDULED",
                homeTeamCrest = null,
                awayTeamCrest = null
            )
        }
    }
}