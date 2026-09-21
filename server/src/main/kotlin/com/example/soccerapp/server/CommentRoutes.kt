package com.example.soccerapp.server

import io.ktor.http.HttpStatusCode
import io.ktor.server.plugins.ContentTransformationException
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route



fun Route.commentRoutes(
    commentRepository: CommentRepository,
) {
    route("/matches/{matchId}/comments") {
        get {//掲示板を開くときに動く用の関数
            val matchId = call.parameters["matchId"]?.toIntOrNull()//callが引数の役割を果たす。application.ktからurlとつながったcallがくる。


            if (matchId == null) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    mapOf("message" to "Invalid matchId")
                )
                return@get
            }

            println("GET comments: matchId=$matchId")

            try {
                val comments = commentRepository.getByMatchId(matchId)

                println("GET comments success: ${comments.size}")

                call.respond(
                    HttpStatusCode.OK,
                    comments
                )
            } catch (e: Exception) {
                println("GET comments failed: ${e::class.qualifiedName}: ${e.message}")
                throw e
            }
        }





        post {//コメント投稿→保存→ui表示をする際に、動く用の関数。
            val matchId = call.parameters["matchId"]?.toIntOrNull()

            if (matchId == null) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    mapOf("message" to "Invalid matchId")
                )
                return@post
            }

            val request = try {//author,textがここで揃う。
                call.receive<CreateCommentRequest>()
            } catch (e: ContentTransformationException) {//無駄なデータ、不足データがある場合
                call.respond(
                    HttpStatusCode.BadRequest,
                    mapOf("message" to "Invalid request body")
                )
                return@post
            }

            if (request.author.isBlank() || request.text.isBlank()) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    mapOf("message" to "Author and text must not be blank")
                )
                return@post
            }

            if (request.author.length > 30 || request.text.length > 500) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    mapOf("message" to "Author or text is too long")
                )
                return@post
            }

            val comment = commentRepository.create(matchId, request)//ここでコメントを保存,  authorなどが一体＝commentになる。

            call.respond(
                HttpStatusCode.Created,
                comment
            )//ここでgetをする場合、get内のfilterによる設計の影響で、計算量が増える。
        }
    }
}

//例:callがもつもの。
//├─ URL：/matches/123/comments
//├─ HTTPメソッド：POST
//├─ ヘッダー：Content-Typeなど
//└─ 本文：{"author":"Sakata","text":"Good match"}