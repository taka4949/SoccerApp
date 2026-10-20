package com.example.soccerapp.server

import io.ktor.server.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.ktor.server.application.install
import kotlinx.coroutines.launch
import kotlinx.coroutines.isActive



fun main() {
    DatabaseFactory.verifyConnection()

    val commentRepository: CommentRepository = JdbcCommentRepository()
    val sessions: SessionService = JdbcSessionService()
    val predictions: PredictionRepository = JdbcPredictionRepository()
    val fixtures: FixtureService = FootballDataFixtureService()
    val workerScope = kotlinx.coroutines.CoroutineScope(
        kotlinx.coroutines.SupervisorJob() + kotlinx.coroutines.Dispatchers.IO
    )
    workerScope.launch {
        while (kotlinx.coroutines.currentCoroutineContext().isActive) {
            try {
                for (matchId in predictions.recentMatchIds()) {
                    try {
                        val match = fixtures.getMatch(matchId)
                        if (match?.status == "FINISHED" && match.homeScore != null && match.awayScore != null) {
                            predictions.settle(matchId, match.homeScore, match.awayScore)
                        }
                        kotlinx.coroutines.delay(1500)
                    } catch (e: Exception) {
                        println("Prediction reconciliation failed for match $matchId: ${e.message}")
                    }
                }
            } catch (e: Exception) {
                println("Prediction reconciliation failed: ${e.message}")
            }
            kotlinx.coroutines.delay(15 * 60 * 1000L)
        }
    }

    embeddedServer(
        factory = Netty,//通信担当
        host = "0.0.0.0",//このpc上で動いているサーバー。このpcが接続先。どの通信入り口でも可能＝0000
        port = 8080,//ktorの受付番号
        module = {
            module(commentRepository, sessions, predictions, fixtures)
        },//関数を渡す
    ).start(wait = true)//サーバー起動後、終了までmainは終了しない。
}

fun Application.module(
    commentRepository: CommentRepository = CommentStoreTest(),//テストではStore()利用
    sessionService: SessionService? = null,
    predictionRepository: PredictionRepository? = null,
    fixtureService: FixtureService? = null,
) {
    install(ContentNegotiation) {//KtorのHTTP通信でJSON変換機能を使用するための設定
        json()
    }


    routing {//通信完了→OKと送る。返信の形指定
        get("/health") {
            call.respondText(
                text = """{"status":"ok"}""",
                contentType = ContentType.Application.Json,
                status = HttpStatusCode.OK,//実際の番号（200）
            )
        }



        commentRoutes(commentRepository, sessionService)//本番
        authRoutes(JdbcAuthRepository())
        if (sessionService != null) sessionRoutes(sessionService)
        if (sessionService != null && predictionRepository != null && fixtureService != null) {
            predictionRoutes(sessionService, predictionRepository, fixtureService)
        }
    }

}


//main()
//↓
//embeddedServer()でKtorサーバーを作る
//↓
//Nettyで8080番を待ち受ける
//↓
//Application.module()を実行

//android→json→ktorサーバー→kotlinに直す→json→androidへ