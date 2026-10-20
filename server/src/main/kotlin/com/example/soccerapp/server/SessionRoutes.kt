package com.example.soccerapp.server

import io.ktor.http.HttpStatusCode
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.ContentTransformationException
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post

fun Route.sessionRoutes(sessions: SessionService) {
    post("/auth/login") {
        val request = try {
            call.receive<LoginRequest>()
        } catch (_: ContentTransformationException) {
            call.respond(HttpStatusCode.BadRequest, mapOf("message" to "Invalid request body"))
            return@post
        } catch (_: BadRequestException) {
            call.respond(HttpStatusCode.BadRequest, mapOf("message" to "Invalid request body"))
            return@post
        }
        if (request.email.length > 254 || request.password.length !in 1..128) {
            call.respond(HttpStatusCode.Unauthorized, mapOf("message" to "Invalid credentials"))
            return@post
        }
        val result = sessions.login(request.email, request.password)
        if (result == null) {
            call.respond(HttpStatusCode.Unauthorized, mapOf("message" to "Invalid credentials"))
            return@post
        }
        call.respond(HttpStatusCode.OK, result)
    }

    get("/auth/me") {
        val id = sessions.resolve(extractBearerToken(call.request.headers["Authorization"]))
        if (id == null) {
            call.respond(HttpStatusCode.Unauthorized, mapOf("message" to "Authentication required"))
            return@get
        }
        call.respond(HttpStatusCode.OK, AccountResponse(id))
    }

    post("/auth/logout") {
        val token = extractBearerToken(call.request.headers["Authorization"])
        if (sessions.resolve(token) == null) {
            call.respond(HttpStatusCode.Unauthorized, mapOf("message" to "Authentication required"))
            return@post
        }
        sessions.logout(token)
        call.respond(HttpStatusCode.NoContent)
    }
}
