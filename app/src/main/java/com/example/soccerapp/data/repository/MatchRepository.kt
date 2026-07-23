package com.example.soccerapp.data.repository


import com.example.soccerapp.data.model.League
import com.example.soccerapp.data.model.Match
import kotlinx.coroutines.delay
import javax.inject.Inject


//なぜ必要か？データの仕入れ先が将来どう変わっても、ViewModelや画面のコードを1行も書き直さなくて済むようにするため
//retrofitなどからデータをとる際に、viewmodel内にロジックを書くと複雑なファイルになってしまう。
//テストを簡単に実行するため。リポジトリがあることであらゆるパターンの安全確認が可能。
class MatchRepository@Inject constructor() : SoccerRepository {//このクラスが必要になったら、このコンストラクタを使えば作れる。


    //ダミーデータは適当な初期値であると同時に、サーバー側の未完成な部分をお互内、アプリ側の作業を独立させるため。
   override suspend  fun getLeagues(): List<League> {
       delay(2000)
        return listOf(
            League(id = "CL", name = "UEFA Champions League"),
            League(id = "Premier", name = "Premier League"),
            League(id = "J1", name = "J-League")
        )
    }

    override suspend fun getMatches(): List<Match> {
        delay(2000)
        return listOf(
            Match(1, "CL", "Real Madrid", "Man City", 0, 0, 10),
            Match(2, "CL", "Bayern", "Arsenal", 1, 0, 45),
            Match(3, "Premier", "Liverpool", "Chelsea", 2, 2, 70),
            Match(4, "J1", "Urawa", "Gamba", 0, 1, 30)
        )
    }
}