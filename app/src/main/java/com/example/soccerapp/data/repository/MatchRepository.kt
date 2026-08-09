package com.example.soccerapp.data.repository


import com.example.soccerapp.data.model.League
import com.example.soccerapp.data.model.Match
import kotlinx.coroutines.delay
import javax.inject.Inject
import com.example.soccerapp.data.remote.api.SoccerApiService


//なぜ必要か？データの仕入れ先が将来どう変わっても、ViewModelや画面のコードを1行も書き直さなくて済むようにするため
//retrofitなどからデータをとる際に、viewmodel内にロジックを書くと複雑なファイルになってしまう。
//テストを簡単に実行するため。リポジトリがあることであらゆるパターンの安全確認が可能。
class MatchRepository@Inject constructor( private val soccerApiService: SoccerApiService
) : SoccerRepository {//このクラスが必要になったら、このコンストラクタを使えば作れる。


   override suspend  fun getLeagues(): List<League> {
       delay(2000)

        val response = soccerApiService.getCompetitions()//ここでデータクラスという全体を手に入れる
        val competitions = response.competitions//ここでリーグ一覧を手に入れる

        return competitions.map { competition ->//compのdtoをList＜league>に変換してる
            League(
                id = competition.code,
                name = competition.name
            )
        }

    }

    override suspend fun getMatches(
        competitionCode: String
    ): List<Match> {
        delay(2000)
        return listOf(
            Match(1, "CL", "Real Madrid", "Man City", 0, 0, 10),
            Match(2, "CL", "Bayern", "Arsenal", 1, 0, 45),
            Match(3, "Premier", "Liverpool", "Chelsea", 2, 2, 70),
            Match(4, "J1", "Urawa", "Gamba", 0, 1, 30)
        ).filter { match ->
            match.leagueId == competitionCode
        }
    }
}

