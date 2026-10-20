package com.example.soccerapp.server

import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.testing.testApplication
import java.time.Instant
import kotlin.test.Test
import kotlin.test.assertEquals

class PredictionRoutesTest {
    private val token = "a".repeat(43)
    private class FakeSessions : SessionService {
        override suspend fun login(email: String, password: String): SessionResponse? = null
        override suspend fun resolve(token: String?): Long? = if (token == "a".repeat(43)) 5 else null
        override suspend fun logout(token: String?) {}
    }
    private class FakeFixtures : FixtureService {
        override suspend fun getMatch(matchId: Int) = FixtureSnapshot(
            matchId, "PL", 2026, Instant.parse("2027-12-01T00:00:00Z"),
            "SCHEDULED", null, null
        )
    }
    private class FakePredictions : PredictionRepository {
        var saved: PredictionResponse? = null
        override suspend fun save(userId: Long, fixture: FixtureSnapshot, home: Int, away: Int): PredictionResponse {
            check(userId == 5L)
            return PredictionResponse(fixture.matchId, home, away, "2026-10-20T00:00:00Z", null)
                .also { saved = it }
        }
        override suspend fun get(userId: Long, matchId: Int): PredictionResponse? = saved
        override suspend fun ranking(league: String, season: Int) = listOf(RankingEntry(1, 5, "Player #5", 3))
        override suspend fun recentMatchIds() = emptyList<Int>()
        override suspend fun settle(matchId: Int, home: Int, away: Int) {}
    }
    @Test fun scoringRules() {
        assertEquals(3, predictionPoints(2, 1, 2, 1))
        assertEquals(1, predictionPoints(2, 1, 1, 0))
        assertEquals(1, predictionPoints(0, 0, 2, 2))
        assertEquals(0, predictionPoints(1, 2, 2, 1))
    }
    @Test fun requiresAuthenticationAndValidatesScores() = testApplication {
        application { module(sessionService = FakeSessions(), predictionRepository = FakePredictions(), fixtureService = FakeFixtures()) }
        assertEquals(HttpStatusCode.Unauthorized, client.put("/matches/111/prediction") {
            contentType(ContentType.Application.Json); setBody("""{"homeScore":2,"awayScore":1}""")
        }.status)
        assertEquals(HttpStatusCode.BadRequest, client.put("/matches/111/prediction") {
            header("Authorization", "Bearer $token")
            contentType(ContentType.Application.Json); setBody("""{"homeScore":-1,"awayScore":1}""")
        }.status)
    }
    @Test fun authenticatedPredictionAndRanking() = testApplication {
        application { module(sessionService = FakeSessions(), predictionRepository = FakePredictions(), fixtureService = FakeFixtures()) }
        val result = client.put("/matches/111/prediction") {
            header("Authorization", "Bearer $token")
            contentType(ContentType.Application.Json)
            setBody("""{"homeScore":2,"awayScore":1}""")
        }
        assertEquals(HttpStatusCode.OK, result.status)
        assertEquals(HttpStatusCode.OK, client.get("/rankings?league=PL&season=2026").status)
    }
}
