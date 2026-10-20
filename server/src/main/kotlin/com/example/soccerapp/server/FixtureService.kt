package com.example.soccerapp.server

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.io.IOException
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration
import java.time.Instant

interface FixtureService {
    suspend fun getMatch(matchId: Int): FixtureSnapshot?
}

class FootballDataFixtureService : FixtureService {
    private val client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(7)).build()
    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun getMatch(matchId: Int): FixtureSnapshot? = withContext(Dispatchers.IO) {
        val token = System.getenv("SOCCER_FOOTBALL_DATA_TOKEN")
            ?.takeIf { it.isNotBlank() }
            ?: throw IllegalStateException("SOCCER_FOOTBALL_DATA_TOKEN is not set")
        val request = HttpRequest.newBuilder()
            .uri(URI.create("https://api.football-data.org/v4/matches/$matchId"))
            .timeout(Duration.ofSeconds(10))
            .header("X-Auth-Token", token)
            .GET()
            .build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        if (response.statusCode() == 404) return@withContext null
        if (response.statusCode() != 200) {
            throw IOException("Football data provider returned HTTP ${response.statusCode()}")
        }
        val data = json.parseToJsonElement(response.body()).jsonObject
        val score = data.getValue("score").jsonObject.getValue("fullTime").jsonObject
        fun JsonObject.text(key: String) = getValue(key).jsonPrimitive.content
        fun JsonObject.optionalInt(key: String): Int? =
            get(key)?.jsonPrimitive?.content?.toIntOrNull()

        FixtureSnapshot(
            matchId = data.text("id").toInt(),
            leagueCode = data.getValue("competition").jsonObject.text("code"),
            seasonYear = data.getValue("season").jsonObject.text("startDate").take(4).toInt(),
            kickoffAt = Instant.parse(data.text("utcDate")),
            status = data.text("status"),
            homeScore = score.optionalInt("home"),
            awayScore = score.optionalInt("away")
        )
    }
}
