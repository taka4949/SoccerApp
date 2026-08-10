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


    override suspend fun getLeagues(): List<League> {
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

        val response = soccerApiService.getMatches(
            competitionCode = competitionCode,
            status = "SCHEDULED"//未定の試合のみ
        )

        val matches = response.matches

        return matches.map { match ->
            Match(
                id = match.id,
                leagueId = match.competition.code,
                homeTeam = match.homeTeam.name ?: "Unknown",
                awayTeam = match.awayTeam.name ?: "Unknown",
                homeScore = match.score.fullTime.home,
                awayScore = match.score.fullTime.away,
                utcDate = match.utcDate,
                status = match.status
            )//ここで依存関係切り離す。ui用データmatch.ktを通して送る。

        }
    }
}


