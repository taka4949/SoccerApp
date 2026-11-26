package com.example.soccerapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.soccerapp.data.model.League
import com.example.soccerapp.data.model.Match
import com.example.soccerapp.ui.LeagueListScreen
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.soccerapp.ui.MatchListScreen
import com.example.soccerapp.ui.route.MatchDetailRoute
import com.example.soccerapp.ui.MatchMenuScreen
import com.example.soccerapp.ui.route.ArchivedThreadsRoute
import com.example.soccerapp.ui.route.ArchivedCommentsRoute
import com.example.soccerapp.ui.PredictionScreen


@Composable
fun AppNavigation(
    leagues: List<League>,
    matches: List<Match>,
    onLeagueSelected: (String) -> Unit,
    favoriteMatchIds: Set<Int>,
    onFavoriteChange: (Int, Boolean) -> Unit
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "leagues"
    ) {
        composable(route = "leagues") {
            LeagueListScreen(
                leagues = leagues,
                onLeagueClick = { leagueId ->//これはLeagueListScreen内で起動する！
                    onLeagueSelected(leagueId)//リーグクリックでloadMatches(leagueId)へ
                    navController.navigate("leagues/$leagueId/matches")
                }
            )
        }

        composable(route = "leagues/{leagueId}/matches") { backStackEntry ->
            val leagueId = requireNotNull(
                backStackEntry.arguments?.getString("leagueId")
            )

            val leagueMatches = matches.filter { match ->
                match.leagueId == leagueId
            }
            MatchListScreen(
                matches = leagueMatches,
                onMatchClick = { matchId ->
                    navController.navigate("matches/$matchId/menu")//遷移
                },
                favoriteMatchIds = favoriteMatchIds,
                onFavoriteChange = onFavoriteChange,
            )
        }

        composable(
            route = "matches/{matchId}/menu",
            arguments = listOf(
                navArgument("matchId") {
                    type = NavType.IntType
                }
            )
        ) {
            backStackEntry ->

            val matchId = requireNotNull(
                backStackEntry.arguments?.getInt("matchId")
            )

            MatchMenuScreen(//メニュー画面表示
                onLiveClick = {
                    navController.navigate("matches/$matchId/comments")
                },
                onArchivesClick = {
                    navController.navigate("matches/$matchId/archives")
                },
                onPredictionClick = {
                    navController.navigate("matches/$matchId/predictions")
                }
            )
        }



        composable(
            route = "matches/{matchId}/predictions",
            arguments = listOf(
                navArgument("matchId") {
                    type = NavType.IntType
                }
            )
        ) {
            PredictionScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }



        composable(
            route = "matches/{matchId}/comments",
            arguments = listOf(
                navArgument("matchId") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->

            val matchId = requireNotNull(
                backStackEntry.arguments?.getInt("matchId")
            )

            MatchDetailRoute(//試合詳細画面へ。
                matchId = matchId
            )
        }

        composable(
            route = "matches/{matchId}/archives",
            arguments = listOf(
                navArgument("matchId") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->

            val matchId = requireNotNull(
                backStackEntry.arguments?.getInt("matchId")
            )

            ArchivedThreadsRoute(
                onThreadClick = { threadId ->
                    navController.navigate(
                        "matches/$matchId/archives/$threadId"
                    )

                }
            )
        }




        composable(
            route = "matches/{matchId}/archives/{threadId}",
            arguments = listOf(
                navArgument("matchId") {
                    type = NavType.IntType
                },
                navArgument("threadId") {
                    type = NavType.LongType
                }
            )
        ) {

            ArchivedCommentsRoute()
        }

    }
}
