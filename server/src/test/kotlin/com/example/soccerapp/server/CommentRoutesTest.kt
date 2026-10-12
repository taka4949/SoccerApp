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







    @Test
    fun getArchivedThreadsReturnsEmptyList() = testApplication {
        application {
            module()
        }

        val response = client.get("/matches/123/threads")

        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("[]", response.bodyAsText())
    }



    @Test
    fun getArchivedThreadsReturnsList() = testApplication {
        val repository = object : CommentRepository by CommentStoreTest() {

            override suspend fun getArchivedThreads(
                matchId: Int
            ): List<ArchivedThreadDto> {
                assertEquals(123, matchId)

                return listOf(
                    ArchivedThreadDto(threadId = 11L, number = 2),
                    ArchivedThreadDto(threadId = 10L, number = 1)
                )
            }
        }

        application {
            module(repository)
        }

        val response = client.get("/matches/123/threads")

        assertEquals(HttpStatusCode.OK, response.status)

        val threads = Json.decodeFromString<List<ArchivedThreadDto>>(
            response.bodyAsText()
        )

        assertEquals(2, threads.size)
        assertEquals(11L, threads[0].threadId)
        assertEquals(2, threads[0].number)
        assertEquals(10L, threads[1].threadId)
        assertEquals(1, threads[1].number)
    }




    @Test
    fun getArchivedThreadsWithInvalidMatchIdReturnsBadRequest() =
        testApplication {
            application {
                module()
            }

            val response = client.get("/matches/abc/threads")

            assertEquals(HttpStatusCode.BadRequest, response.status)
        }




    @Test
    fun getArchivedCommentsReturnsSelectedThreadComments() = testApplication {

        val repository = object : CommentRepository by CommentStoreTest() {



            override suspend fun getArchivedComments(
                matchId: Int,
                threadId: Long
            ): List<Comment> {

                assertEquals(123, matchId)
                assertEquals(15L, threadId)

                return listOf(
                    Comment(
                        id = 101L,
                        matchId = 123,
                        threadId = 15L,
                        author = "Taro",
                        text = "First archived comment",
                        createdAt = "2026-10-01T10:00:00Z"
                    ),
                    Comment(
                        id = 102L,
                        matchId = 123,
                        threadId = 15L,
                        author = "Hanako",
                        text = "Second archived comment",
                        createdAt = "2026-10-01T10:01:00Z"
                    )
                )
            }
        }

        application {
            module(repository)
        }

        val response = client.get(
            "/matches/123/threads/15/comments"
        )

        assertEquals(HttpStatusCode.OK, response.status)

        val comments = Json.decodeFromString<List<Comment>>(
            response.bodyAsText()
        )

        assertEquals(2, comments.size)

        assertEquals(101L, comments[0].id)
        assertEquals(15L, comments[0].threadId)
        assertEquals("Taro", comments[0].author)
        assertEquals("First archived comment", comments[0].text)

        assertEquals(102L, comments[1].id)
        assertEquals(15L, comments[1].threadId)
        assertEquals("Hanako", comments[1].author)
        assertEquals("Second archived comment", comments[1].text)
    }



    @Test
    fun getArchivedCommentsReturnsEmptyList() = testApplication {
        application {
            module()
        }

        val response = client.get(
            "/matches/123/threads/15/comments"
        )

        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("[]", response.bodyAsText())
    }

    @Test
    fun getArchivedCommentsWithInvalidMatchIdReturnsBadRequest() = testApplication {
        application {
            module()
        }

        val response = client.get(
            "/matches/abc/threads/15/comments"
        )

        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @Test
    fun getArchivedCommentsWithInvalidThreadIdReturnsBadRequest() = testApplication {
        application {
            module()
        }

        val response = client.get(
            "/matches/123/threads/abc/comments"
        )

        assertEquals(HttpStatusCode.BadRequest, response.status)
    }
}