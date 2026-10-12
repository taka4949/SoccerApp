package com.example.soccerapp.server

import kotlinx.coroutines.runBlocking
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals



class JdbcCommentRepositoryIntegrationTest {

    @Test
    fun switchesThreadAfter1000Comments() = runBlocking {

        // 接続先をテスト用DBへ→DB名を確認
        DatabaseFactory.getConnection().use { connection ->
            connection.createStatement().use { statement ->
                statement.executeQuery(
                    "SELECT current_database()"
                ).use { result ->
                    check(result.next())
                    check(result.getString(1) == "soccer_app_test") {
                        "テスト用DB以外への書き込みは禁止"
                    }
                }
            }
        }

        val repository = JdbcCommentRepository()


        val matchId = Random.nextInt(
            1_000_000_000,
            2_000_000_000
        )

        //スレッド作成
        val first = repository.create(
            matchId,
            CreateCommentRequest("test", "first")
        )

        val oldThreadId = first.threadId

        //合計999件のスレッドへ
        DatabaseFactory.getConnection().use { connection ->
            connection.prepareStatement(
                """
                INSERT INTO comments (thread_id, author, content)
                SELECT ?, 'test-user', 'test-comment'
                FROM generate_series(1, 998) 
                """.trimIndent()
            ).use { statement ->
                statement.setLong(1, oldThreadId)
                assertEquals(998, statement.executeUpdate())
            }
        }

        //1000件目を投稿
        val comment1000 = repository.create(
            matchId,
            CreateCommentRequest("test", "comment-1000")
        )

        val before = repository.getArchivedThreads(matchId)
        assertEquals(0, before.size)



        //1001件目を投稿
        val comment1001 = repository.create(
            matchId,
            CreateCommentRequest("test", "comment-1001")
        )

        //スレッドIDが切り替わったことを確認
        assertEquals(oldThreadId, comment1000.threadId)
        assertNotEquals(oldThreadId, comment1001.threadId)//違うIDになる


        val archived = repository.getArchivedThreads(matchId)

        assertEquals(1, archived.size)
        assertEquals(oldThreadId, archived[0].threadId)
        assertEquals(1, archived[0].number)


        //PostgreSQL状態を確認
        DatabaseFactory.getConnection().use { connection ->
            connection.prepareStatement(
                """
                SELECT t.id, t.number, t.status,
                       COUNT(c.id) AS comment_count
                FROM threads t
                LEFT JOIN comments c ON c.thread_id = t.id
                WHERE t.match_id = ?
                GROUP BY t.id, t.number, t.status
                ORDER BY t.number
                """.trimIndent()
            ).use { statement ->

                statement.setInt(1, matchId)

                statement.executeQuery().use { result ->


                    // 旧スレッド
                    check(result.next())
                    assertEquals(oldThreadId, result.getLong("id"))
                    assertEquals(1, result.getInt("number"))
                    assertEquals("CLOSED", result.getString("status"))
                    assertEquals(1000L, result.getLong("comment_count"))





                    // 新スレッド
                    check(result.next())
                    assertEquals(
                        comment1001.threadId,
                        result.getLong("id")
                    )
                    assertEquals(2, result.getInt("number"))
                    assertEquals("OPEN", result.getString("status"))
                    assertEquals(1L, result.getLong("comment_count"))






                    // スレッドが2つかどうか確認。
                    assertFalse(result.next())
                }
            }
        }
    }
}