package com.example.soccerapp.server

import io.ktor.http.HttpStatusCode
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.ContentTransformationException
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.put
import java.time.Instant

fun Route.predictionRoutes(
    sessions: SessionService,
    predictions: PredictionRepository,
    fixtures: FixtureService
) {
    put("/matches/{matchId}/prediction") {
        val userId = sessions.resolve(extractBearerToken(call.request.headers["Authorization"]))
        if (userId == null) {
            call.respond(HttpStatusCode.Unauthorized, mapOf("message" to "Authentication required"))
            return@put
        }
        val matchId = call.parameters["matchId"]?.toIntOrNull()?.takeIf { it > 0 }
        if (matchId == null) {
            call.respond(HttpStatusCode.BadRequest, mapOf("message" to "Invalid matchId"))
            return@put
        }
        val request = try {
            call.receive<SavePredictionRequest>()
        } catch (_: ContentTransformationException) {
            call.respond(HttpStatusCode.BadRequest, mapOf("message" to "Invalid request body"))
            return@put
        } catch (_: BadRequestException) {
            call.respond(HttpStatusCode.BadRequest, mapOf("message" to "Invalid request body"))
            return@put
        }
        if (request.homeScore !in 0..20 || request.awayScore !in 0..20) {
            call.respond(HttpStatusCode.BadRequest, mapOf("message" to "Score must be between 0 and 20"))
            return@put
        }

        val match = try {
            fixtures.getMatch(matchId)
        } catch (e: Exception) {
            call.respond(HttpStatusCode.ServiceUnavailable, mapOf("message" to "Fixture provider unavailable"))
            return@put
        }
        if (match == null || match.matchId != matchId) {
            call.respond(HttpStatusCode.NotFound, mapOf("message" to "Match not found"))
            return@put
        }
        if (match.status !in setOf("SCHEDULED", "TIMED") || !match.kickoffAt.isAfter(Instant.now())) {
            call.respond(HttpStatusCode.Conflict, mapOf("message" to "Prediction deadline passed"))
            return@put
        }
        val saved = predictions.save(userId, match, request.homeScore, request.awayScore)
        if (saved == null) {
            call.respond(HttpStatusCode.Conflict, mapOf("message" to "Prediction deadline passed"))
            return@put
        }
        call.respond(HttpStatusCode.OK, saved)
    }

    get("/matches/{matchId}/prediction") {
        val userId = sessions.resolve(extractBearerToken(call.request.headers["Authorization"]))
        if (userId == null) {
            call.respond(HttpStatusCode.Unauthorized, mapOf("message" to "Authentication required"))
            return@get
        }
        val matchId = call.parameters["matchId"]?.toIntOrNull()?.takeIf { it > 0 }
        if (matchId == null) {
            call.respond(HttpStatusCode.BadRequest, mapOf("message" to "Invalid matchId"))
            return@get
        }
        val saved = predictions.get(userId, matchId)
        if (saved == null) {
            call.respond(HttpStatusCode.NotFound, mapOf("message" to "Prediction not found"))
            return@get
        }
        call.respond(HttpStatusCode.OK, saved)
    }

    get("/rankings") {
        val league = call.request.queryParameters["league"]
            ?.trim()?.uppercase()
            ?.takeIf { Regex("^[A-Z0-9]{2,10}$").matches(it) }
        val season = call.request.queryParameters["season"]?.toIntOrNull()
            ?.takeIf { it in 2000..2100 }
        if (league == null || season == null) {
            call.respond(HttpStatusCode.BadRequest, mapOf("message" to "Invalid league or season"))
            return@get
        }
        call.respond(HttpStatusCode.OK, predictions.ranking(league, season))
    }
}
