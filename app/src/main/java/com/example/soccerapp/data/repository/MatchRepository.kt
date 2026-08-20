package com.example.soccerapp.data.repository


import com.example.soccerapp.data.local.dao.MatchDao
import com.example.soccerapp.data.local.entity.MatchEntity
import com.example.soccerapp.data.model.League
import com.example.soccerapp.data.model.Match
import kotlinx.coroutines.delay
import javax.inject.Inject
import com.example.soccerapp.data.remote.api.SoccerApiService
import kotlinx.coroutines.CancellationException


//なぜ必要か？データの仕入れ先が将来どう変わっても、ViewModelや画面のコードを1行も書き直さなくて済むようにするため
//retrofitなどからデータをとる際に、viewmodel内にロジックを書くと複雑なファイルになってしまう。
//テストを簡単に実行するため。リポジトリがあることであらゆるパターンの安全確認が可能。
class MatchRepository@Inject constructor(
    private val soccerApiService: SoccerApiService,
    private val matchDao: MatchDao
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

        return try {
            val response = soccerApiService.getMatches(
                competitionCode = competitionCode,
                status = "SCHEDULED" // 未定の試合のみ
            )

            val matches = response.matches // APIから取得

            val matchEntities = matches.map { match ->
                MatchEntity(
                    id = match.id,
                    leagueId = match.competition.code,
                    homeTeam = match.homeTeam.name ?: "Unknown",
                    awayTeam = match.awayTeam.name ?: "Unknown",
                    homeScore = match.score.fullTime.home,
                    awayScore = match.score.fullTime.away,
                    utcDate = match.utcDate,
                    status = match.status
                )
            }

            matchDao.upsertMatches(matchEntities) // Roomへ保存、更新

            matches.map { match ->
                Match(
                    id = match.id,
                    leagueId = match.competition.code,
                    homeTeam = match.homeTeam.name ?: "Unknown",
                    awayTeam = match.awayTeam.name ?: "Unknown",
                    homeScore = match.score.fullTime.home,
                    awayScore = match.score.fullTime.away,
                    utcDate = match.utcDate,
                    status = match.status
                ) // ここで依存関係を切り離す。UI用データMatch.ktを通して送る。
            }
        }  catch (e: CancellationException) {//アプリを完全終了し、ui表示が必要ではなくなった場合の処理。
            throw e
        } catch (e: Exception) {
            val cachedMatches = matchDao.getMatchesByLeague(
                competitionCode
            ) // 通信失敗時にRoomから取得

            cachedMatches.map { match ->
                Match(
                    id = match.id,
                    leagueId = match.leagueId,
                    homeTeam = match.homeTeam,
                    awayTeam = match.awayTeam,
                    homeScore = match.homeScore,
                    awayScore = match.awayScore,
                    utcDate = match.utcDate,
                    status = match.status
                ) // MatchEntityからUI用のMatchへ変換
            }
        }
    }
}
