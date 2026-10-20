package com.example.soccerapp.server

import io.ktor.http.HttpStatusCode
import io.ktor.server.plugins.ContentTransformationException
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route



fun Route.commentRoutes(//HTTP通信
    commentRepository: CommentRepository,
    sessionService: SessionService? = null,
) {


    route("/matches/{matchId}/threads") {
        get {
            val matchId = call.parameters["matchId"]?.toIntOrNull()

            if (matchId == null) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    mapOf("message" to "Invalid matchId")
                )
                return@get
            }

            val threads = commentRepository.getArchivedThreads(matchId)//nullチェック後の必要がある。

            call.respond(
                HttpStatusCode.OK,
                threads
            )
        }
    }



    route("/matches/{matchId}/comments") {
        get {//コメント欄返す用
            val matchId = call.parameters["matchId"]?.toIntOrNull()


            if (matchId == null) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    mapOf("message" to "Invalid matchId")
                )
                return@get
            }


            try {
                val comments = commentRepository.getByMatchId(matchId)//コメント欄返ってくる


                call.respond(//Retrofitへ
                    HttpStatusCode.OK,
                    comments
                )


            } catch (e: Exception) {
                println("GET comments failed: ${e::class.qualifiedName}: ${e.message}")
                throw e
            }
        }





        post {//コメント投稿の1件返す用
            if (sessionService != null) {
                val userId = sessionService.resolve(extractBearerToken(call.request.headers["Authorization"]))
                if (userId == null) {
                    call.respond(
                        HttpStatusCode.Unauthorized,
                        mapOf("message" to "Authentication required")
                    )
                    return@post
                }
            }
            val matchId = call.parameters["matchId"]?.toIntOrNull()

            if (matchId == null) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    mapOf("message" to "Invalid matchId")
                )
                return@post
            }

            val request = try {
                call.receive<CreateCommentRequest>()//author,text取得。


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

            val comment = commentRepository.create(matchId, request)//ここでコメントを保存。Comment()返ってくる。



            call.respond(//Retrofitへ
                HttpStatusCode.Created,
                comment
            )
        }
    }




    route("/matches/{matchId}/threads/{threadId}/comments") {

        get {
            val matchId = call.parameters["matchId"]?.toIntOrNull()
            val threadId = call.parameters["threadId"]?.toLongOrNull()

            if (matchId == null || threadId == null) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    mapOf("message" to "Invalid matchId or threadId")
                )
                return@get
            }

            val comments = commentRepository.getArchivedComments(
                matchId = matchId,
                threadId = threadId
            )

            call.respond(
                HttpStatusCode.OK,
                comments
            )
        }
    }
}





