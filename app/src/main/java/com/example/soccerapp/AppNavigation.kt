package com.example.soccerapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.soccerapp.data.model.League
import com.example.soccerapp.data.model.Match
import com.example.soccerapp.ui.LeagueListScreen
import com.example.soccerapp.ui.MatchThreadScreen
import androidx.compose.material3.Text
import com.example.soccerapp.ui.MatchListScreen

@Composable
fun AppNavigation(
    leagues: List<League>,
    matches: List<Match>,
    onLeagueSelected: (String) -> Unit//この関数と引数で返り値は無し、というセットを下に渡す。
) {
    val navController = rememberNavController()//naviはスタック、変遷の履歴を記録する。戻ることが可能へ

    NavHost(
        navController = navController,
        startDestination = "league_list"
    ) {
        composable(route = "league_list") {
            LeagueListScreen(
                leagues = leagues,
                onLeagueClick = { leagueId ->//これはleaguelistscreen.kt内で起動する！
                    onLeagueSelected(leagueId)
                    navController.navigate("league/$leagueId")///.navigateがバックスタックに放り込む関数！（大事）。画面名を変更する関数でもある。ここからは直接ID入手不可。｛｝だから。
                }
            )
        }

        composable(route = "league/{leagueId}") { backStackEntry ->
            val leagueId = requireNotNull(
                backStackEntry.arguments?.getString("leagueId")
            )

            val leagueMatches = matches.filter { match ->
                match.leagueId == leagueId
            }
            MatchListScreen(
                matches = leagueMatches,
                onMatchClick = { matchId ->
                    navController.navigate("match/$matchId")//.navigate()は保存。
                }
            )
        }
        composable(route = "match/{matchId}") { backStackEntry ->
            val matchId = requireNotNull(
                backStackEntry.arguments?.getString("matchId")
            ).toInt()

            val selectedMatch = matches.firstOrNull { match ->
                match.id == matchId
            }

            if (selectedMatch == null) {
                Text(
                    text = "Loading..."
                )
            } else
            MatchThreadScreen(
                match = selectedMatch
            )
        }
    }
}


