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
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.soccerapp.ui.MatchListScreen
import com.example.soccerapp.ui.screen.MatchDetailRoute

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
                onLeagueClick = { leagueId ->//これはLeagueListScreen内で起動する！
                    onLeagueSelected(leagueId)//リーグクリック（＝マッチリスト表示）で、loadMatches(leagueId)へ。onLeagueSelected=loadMatches
                    navController.navigate("league/$leagueId")///.navigateがバックスタックに放り込む関数！（大事）。画面名を変更する関数でもある。ここからは直接ID入手不可。｛｝だから。
                }//ここで、クリックが下の処理へ行くトリガー。リーグIDとその画面情報を保存。
            )
        }




        composable(route = "league/{leagueId}") { backStackEntry ->
            val leagueId = requireNotNull(
                backStackEntry.arguments?.getString("leagueId")
            )

            val leagueMatches = matches.filter { match ->
                match.leagueId == leagueId//二重チェックしている。既にapi側（ApiService)で選別済み。
            }
            MatchListScreen(//リーグ内の試合リスト
                matches = leagueMatches,
                onMatchClick = { matchId ->
                    navController.navigate("match/$matchId")//.navigate()は保存。試合をタップが下のトリガー。
                }//マッチクリックしたら、navigate()にマッチIｄをいれて下の関数↓が動く。
            )
        }



        composable(
            route = "match/{matchId}",
            arguments = listOf(
                navArgument("matchId") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->

            val matchId = requireNotNull(
                backStackEntry.arguments?.getInt("matchId")
            )

            MatchDetailRoute(
                matchId = matchId
            )
        }

    }
}


