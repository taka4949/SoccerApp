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



fun main() {
    embeddedServer(
        factory = Netty,//通信担当
        host = "0.0.0.0",//このpc上で動いているサーバー。このpcが接続先。どの通信入り口でも可能＝0000
        port = 8080,//ktorの受付番号
        module = Application::module,//関数を渡す
    ).start(wait = true)//サーバー起動後、終了までmainは終了しない。
}

fun Application.module() {
    install(ContentNegotiation) {//KtorのHTTP通信でJSON変換機能を使用するための設定。大事。
        json()
    }

    val commentStore = CommentStore()


    routing {//URLごとの処理を登録するktor関数
        get("/health") {
            call.respondText(
                text = """{"status":"ok"}""",
                contentType = ContentType.Application.Json,
                status = HttpStatusCode.OK,//実際の番号（200）
            )
        }

        commentRoutes(commentStore)
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