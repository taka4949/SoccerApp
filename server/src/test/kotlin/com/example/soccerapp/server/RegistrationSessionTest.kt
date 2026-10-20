package com.example.soccerapp.server

import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.testing.testApplication
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class RegistrationSessionTest {
    @Test
    fun registeringIssuesSessionWithoutRequiringSecondLogin() = testApplication {
        val account = object : AuthRepository {
            override suspend fun register(request: RegisterRequest): RegisterResponse {
                assertEquals("new@example.com", request.email)
                return RegisterResponse(42, request.email)
            }
        }
        val sessions = object : SessionService {
            override suspend fun issueForUser(userId: Long): SessionResponse {
                assertEquals(42, userId)
                return SessionResponse("a".repeat(43), "2026-11-20T00:00:00Z", userId)
            }
            override suspend fun login(email: String, password: String): SessionResponse? = null
            override suspend fun resolve(token: String?): Long? = null
            override suspend fun logout(token: String?) {}
        }
        application { module(sessionService = sessions, authRepository = account) }

        val response = client.post("/auth/register") {
            contentType(ContentType.Application.Json)
            setBody("""{"email":"new@example.com","password":"a-valid-long-password"}""")
        }
        assertEquals(HttpStatusCode.Created, response.status)
        val body = Json.decodeFromString<RegisterSessionResponse>(response.bodyAsText())
        assertEquals(42, body.id)
        assertEquals("new@example.com", body.email)
        assertEquals("a".repeat(43), body.accessToken)
    }
}
