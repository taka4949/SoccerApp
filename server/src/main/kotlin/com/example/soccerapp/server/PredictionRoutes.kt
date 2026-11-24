
package com.example.soccerapp.server

import io.ktor.http.HttpStatusCode
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.ContentTransformationException
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route

fun Route.predictionRoutes(
    repository: PredictionRepository,
    sessionService: SessionService
) {

    route("/matches/{matchId}/predictions") {

        post {
            val matchId = call.parameters["matchId"]?.toIntOrNull()

            if (matchId == null || matchId <= 0) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    mapOf("message" to "Invalid matchId")
                )
                return@post
            }





            val token = extractBearerToken(
                call.request.headers["Authorization"]
            )

            val userId = sessionService.resolve(token)

            if (userId == null) {
                call.respond(
                    HttpStatusCode.Unauthorized,
                    mapOf("message" to "Authentication required")
                )
                return@post
            }




            val request = try {
                call.receive<CreatePredictionRequest>()
            } catch (e: ContentTransformationException) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    mapOf("message" to "Invalid request body")
                )
                return@post
            } catch (e: BadRequestException) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    mapOf("message" to "Invalid request body")
                )
                return@post
            }

            if (
                request.predictedHomeScore < 0 ||
                request.predictedAwayScore < 0
            ) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    mapOf("message" to "Scores must be 0 or greater")
                )
                return@post
            }






            try {
                val prediction = repository.create(
                    userId = userId,
                    matchId = matchId,
                    request = request
                )

                call.respond(
                    HttpStatusCode.Created,
                    prediction
                )

            } catch (e: PredictionAlreadyExistsException) {
                call.respond(
                    HttpStatusCode.Conflict,
                    mapOf("message" to "Prediction already exists")
                )
            }
        }




        get("/me") {
            val matchId = call.parameters["matchId"]?.toIntOrNull()

            if (matchId == null || matchId <= 0) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    mapOf("message" to "Invalid matchId")
                )
                return@get
            }

            val token = extractBearerToken(
                call.request.headers["Authorization"]
            )

            val userId = sessionService.resolve(token)

            if (userId == null) {
                call.respond(
                    HttpStatusCode.Unauthorized,
                    mapOf("message" to "Authentication required")
                )
                return@get
            }




            val prediction = repository.getMyPrediction(
                userId = userId,
                matchId = matchId
            )

            if (prediction == null) {
                call.respond(
                    HttpStatusCode.NotFound,
                    mapOf("message" to "Prediction not found")
                )
                return@get
            }

            call.respond(
                HttpStatusCode.OK,
                prediction
            )
        }
    }
}
