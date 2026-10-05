package com.example.soccerapp.data.repository


import com.example.soccerapp.data.local.dao.MatchDao
import com.example.soccerapp.data.local.entity.MatchEntity
import com.example.soccerapp.data.model.League
import com.example.soccerapp.data.model.Match
import javax.inject.Inject
import com.example.soccerapp.data.remote.api.SoccerApiService
import kotlinx.coroutines.CancellationException
import com.example.soccerapp.data.local.dao.LeagueDao
import com.example.soccerapp.data.local.entity.LeagueEntity


class MatchRepository@Inject constructor(
    private val soccerApiService: SoccerApiService,
    private val matchDao: MatchDao,
    private val leagueDao: LeagueDao
) : SoccerRepository {//このクラスが必要になったら、このコンストラクタを使えば作れる。


    override suspend fun getCachedLeagues(): List<League> {//先にroomキャッシュから見る
        return leagueDao.getLeagues().map { league ->
            League(
                id = league.id,
                name = league.name,
                emblem = league.emblem
            )
        }
    }



    override suspend fun getLeagues(): List<League> {


        return try {


            val response = soccerApiService.getCompetitions()//ここでデータクラスという全体を手に入れる
            val competitions = response.competitions//ここでリーグ一覧を手に入れる

            val leagueEntities = competitions.map { competition ->
                LeagueEntity(
                    id = competition.code,
                    name = competition.name,
                    emblem = competition.emblem
                )
            }

            leagueDao.upsertLeagues(leagueEntities)


            competitions.map { competition ->
                League(
                    id = competition.code,
                    name = competition.name,
                    emblem = competition.emblem
                )
            }

        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            val cachedLeagues = leagueDao.getLeagues()

            if (cachedLeagues.isEmpty()) {
                throw e
            }

            cachedLeagues.map { league ->
                League(
                    id = league.id,
                    name = league.name,
                    emblem = league.emblem
                )
            }
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

            val matches = response.matches // APIから取得(Dto)

            val matchEntities = matches.map { match ->//Dto→Entities
                MatchEntity(
                    id = match.id,
                    leagueId = match.competition.code,
                    homeTeam = match.homeTeam.name ?: "Unknown",
                    awayTeam = match.awayTeam.name ?: "Unknown",
                    homeScore = match.score.fullTime.home,
                    awayScore = match.score.fullTime.away,
                    utcDate = match.utcDate,
                    status = match.status,
                    homeTeamCrest = match.homeTeam.crest,
                    awayTeamCrest = match.awayTeam.crest,
                )
            }

            matchDao.upsertMatches(matchEntities) // Roomへ保存、更新

            matches.map { match ->//Dto→Match.ktへ
                Match(
                    id = match.id,
                    leagueId = match.competition.code,
                    homeTeam = match.homeTeam.name ?: "Unknown",
                    awayTeam = match.awayTeam.name ?: "Unknown",
                    homeScore = match.score.fullTime.home,
                    awayScore = match.score.fullTime.away,
                    utcDate = match.utcDate,
                    status = match.status,
                    homeTeamCrest = match.homeTeam.crest,
                    awayTeamCrest = match.awayTeam.crest,
                ) // ここで依存関係を切り離す。UI用データMatch.ktを通して送る。
            }





        }  catch (e: CancellationException) {//アプリを完全終了し、ui表示が必要ではなくなった場合の処理。
            throw e
        } catch (e: Exception) {
            val cachedMatches = matchDao.getMatchesByLeague(
                competitionCode
            ) // 通信失敗時にRoomから取得=getMatchesByLeague()

            if (cachedMatches.isEmpty()) {
                throw e
            }
            

            cachedMatches.map { match ->//Entities→Match.ktへ
                Match(
                    id = match.id,
                    leagueId = match.leagueId,
                    homeTeam = match.homeTeam,
                    awayTeam = match.awayTeam,
                    homeScore = match.homeScore,
                    awayScore = match.awayScore,
                    utcDate = match.utcDate,
                    status = match.status,
                    homeTeamCrest = match.homeTeamCrest,
                    awayTeamCrest = match.awayTeamCrest,
                )
            }
        }
    }



    override suspend fun getMatchById(
        matchId: Int
    ): Match? {

        val match = matchDao.getMatchById(matchId)
            ?: return null

        return Match(
            id = match.id,
            leagueId = match.leagueId,
            homeTeam = match.homeTeam,
            awayTeam = match.awayTeam,
            homeScore = match.homeScore,
            awayScore = match.awayScore,
            utcDate = match.utcDate,
            status = match.status,
            homeTeamCrest = match.homeTeamCrest,
            awayTeamCrest = match.awayTeamCrest
        )
    }
}
