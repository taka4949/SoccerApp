package com.example.soccerapp.server

import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class SessionRoutesTest {
    private val token = "a".repeat(43)
    private class FakeSessions : SessionService {
        override suspend fun issueForUser(userId: Long): SessionResponse =
            SessionResponse("a".repeat(43), "2027-01-01T00:00:00Z", userId)
        override suspend fun login(email: String, password: String): SessionResponse? =
            if (email == "demo@example.com" && password == "correct-password")
                SessionResponse("a".repeat(43), "2027-01-01T00:00:00Z", 11) else null

        override suspend fun resolve(token: String?): Long? =
            if (token == "a".repeat(43)) 11 else null

        override suspend fun logout(token: String?) {}
    }

    @Test fun bearerParsingRejectsInvalidHeaders() {
        assertNull(extractBearerToken("Basic abc"))
        assertNull(extractBearerToken("Bearer short"))
        assertEquals(token, extractBearerToken("Bearer $token"))
    }

    @Test fun loginAndMeRequireValidCredentials() = testApplication {
        application { module(sessionService = FakeSessions()) }
        val bad = client.post("/auth/login") {
            contentType(ContentType.Application.Json)
            setBody("""{"email":"demo@example.com","password":"wrong"}""")
        }
        assertEquals(HttpStatusCode.Unauthorized, bad.status)

        val loggedIn = client.post("/auth/login") {
            contentType(ContentType.Application.Json)
            setBody("""{"email":"demo@example.com","password":"correct-password"}""")
        }
        assertEquals(HttpStatusCode.OK, loggedIn.status)
        assertEquals(HttpStatusCode.Unauthorized, client.get("/auth/me").status)
        val me = client.get("/auth/me") { header("Authorization", "Bearer $token") }
        assertEquals(HttpStatusCode.OK, me.status)
    }

}
