package com.example.soccerapp.server



import io.ktor.http.HttpStatusCode
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.ContentTransformationException
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post




private val emailPattern =
    Regex("""^[^\s@]+@[^\s@]+\.[^\s@]+$""")//形式チェック



fun Route.authRoutes(//HTTP通信
    authRepository: AuthRepository,
    sessionService: SessionService? = null
) {
    post("/auth/register") {


        val request = try {
            call.receive<RegisterRequest>()//メールアドレスとパスワード受け取る



        } catch (e: ContentTransformationException) {//受信データを要求された型に変換できない場合の例外
            call.respond(
                HttpStatusCode.BadRequest,
                mapOf("message" to "Invalid request body")
            )
            return@post



        } catch (e: BadRequestException) {//不正なHTTPリクエストを処理しようとして発生する例外
            call.respond(
                HttpStatusCode.BadRequest,
                mapOf("message" to "Invalid request body")
            )
            return@post
        }

        val email = request.email.trim()

        if (
            email.length > 254 ||
            !emailPattern.matches(email)//254=一般的なメールアドレスの最大長
        ) {
            call.respond(
                HttpStatusCode.BadRequest,
                mapOf("message" to "Invalid email")
            )
            return@post
        }

        if (request.password.length !in 12..128) {
            call.respond(
                HttpStatusCode.BadRequest,
                mapOf("message" to "Invalid password length")
            )
            return@post
        }

        try {
            val result = authRepository.register(request)

            if (sessionService == null) {
                call.respond(HttpStatusCode.Created, result)
            } else {
                val session = sessionService.issueForUser(result.id)
                call.respond(
                    HttpStatusCode.Created,
                    RegisterSessionResponse(
                        id = result.id,
                        email = result.email,
                        accessToken = session.accessToken,
                        expiresAt = session.expiresAt
                    )
                )
            }

        } catch (e: EmailAlreadyRegisteredException) {
            call.respond(
                HttpStatusCode.Conflict,
                mapOf("message" to "Email already registered")
            )
        }
    }
}