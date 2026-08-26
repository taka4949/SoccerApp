package com.example.soccerapp.server

import io.ktor.http.contentType
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json


//サーバー構築後、コーティングミスなどを検知するためにテストする。

class CommentRoutesTest {

    @Test
    fun postCommentReturnsCreated() = testApplication {
        application {//application.ktの設定反映
            module()
        }

        val response = client.post("/matches/123/comments") {
            contentType(ContentType.Application.Json)//json形式
            setBody(
                """
                {
                    "author": "Taro",
                    "text": "Good match"
                }
                """.trimIndent()
            )
        }

        assertEquals(HttpStatusCode.Created, response.status)
    }

    @Test
    fun getCommentsReturnsPostedComment() = testApplication {
        application {
            module()
        }

        client.post("/matches/123/comments") {
            contentType(ContentType.Application.Json)
            setBody(
                """
                {
                    "author": "Taro",
                    "text": "Good match"
                }
                """.trimIndent()
            )
        }

        val response = client.get("/matches/123/comments")

        assertEquals(HttpStatusCode.OK, response.status)

        val comments = Json.decodeFromString<List<Comment>>(
            response.bodyAsText()
        )

        assertEquals(1, comments.size)
        assertEquals("Taro", comments[0].author)
        assertEquals("Good match", comments[0].text)
    }


    //不正matchIDを検知
    @Test
    fun postCommentWithInvalidMatchIdReturnsBadRequest() = testApplication {
        application {
            module()
        }

        val response = client.post("/matches/abc/comments") {
            contentType(ContentType.Application.Json)
            setBody(
                """
            {
                "author": "Taro",
                "text": "Good match"
            }
            """.trimIndent()
            )
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
    }


    @Test
    fun postCommentWithMalformedJsonReturnsBadRequest() = testApplication {
        application {
            module()
        }

        val response = client.post("/matches/123/comments") {
            contentType(ContentType.Application.Json)
            setBody(
                """
            {
                "author": "Taro",
                "text":
            }
            """.trimIndent()
            )
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @Test
    fun postCommentWithMissingTextReturnsBadRequest() = testApplication {
        application {
            module()
        }

        val response = client.post("/matches/123/comments") {
            contentType(ContentType.Application.Json)
            setBody(
                """
            {
                "author": "Taro"
            }
            """.trimIndent()
            )
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @Test
    fun postCommentWithEmptyTextReturnsBadRequest() = testApplication {
        application {
            module()
        }

        val response = client.post("/matches/123/comments") {
            contentType(ContentType.Application.Json)
            setBody(
                """
            {
                "author": "Taro",
                "text": ""
            }
            """.trimIndent()
            )
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
    }


    @Test
    fun postCommentWithTooLongAuthorReturnsBadRequest() = testApplication {
        application {
            module()
        }

        val author = "a".repeat(31)

        val response = client.post("/matches/123/comments") {
            contentType(ContentType.Application.Json)
            setBody(
                """
            {
                "author": "$author",
                "text": "Good match"
            }
            """.trimIndent()
            )
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @Test
    fun postCommentWithTooLongTextReturnsBadRequest() = testApplication {
        application {
            module()
        }

        val text = "a".repeat(501)

        val response = client.post("/matches/123/comments") {
            contentType(ContentType.Application.Json)
            setBody(
                """
            {
                "author": "Taro",
                "text": "$text"
            }
            """.trimIndent()
            )
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @Test
    fun postCommentWithEmptyAuthorReturnsBadRequest() = testApplication {
        application {
            module()
        }

        val response = client.post("/matches/123/comments") {
            contentType(ContentType.Application.Json)
            setBody(
                """
            {
                "author": "",
                "text": "Good match"
            }
            """.trimIndent()
            )
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
    }
}