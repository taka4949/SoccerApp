package com.example.soccerapp.server

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class JdbcCommentRepository : CommentRepository {

    override suspend fun create(
        matchId: Int,
        request: CreateCommentRequest,
    ): Comment = withContext(Dispatchers.IO) {
        DatabaseFactory.getConnection().use { connection ->
            connection.autoCommit = false
            try {
                // Serialize thread creation for this match within the transaction.
                connection.prepareStatement(
                    "SELECT pg_advisory_xact_lock(?)"
                ).use { statement ->
                    statement.setLong(1, matchId.toLong())
                    statement.executeQuery().use { result -> result.next() }
                }

                val existingThreadId = connection.prepareStatement(
                    """
                    SELECT id
                    FROM threads
                    WHERE match_id = ?
                      AND status = 'OPEN'
                    ORDER BY number DESC
                    LIMIT 1
                    """.trimIndent()
                ).use { statement ->
                    statement.setLong(1, matchId.toLong())
                    statement.executeQuery().use { result ->
                        if (result.next()) result.getLong("id") else null
                    }
                }

                val threadId = existingThreadId ?: connection.prepareStatement(
                    """
                    INSERT INTO threads (match_id, number)
                    SELECT ?, COALESCE(MAX(number), 0) + 1
                    FROM threads
                    WHERE match_id = ?
                    RETURNING id
                    """.trimIndent()
                ).use { statement ->
                    statement.setLong(1, matchId.toLong())
                    statement.setLong(2, matchId.toLong())
                    statement.executeQuery().use { result ->
                        check(result.next()) { "Created thread was not returned" }
                        result.getLong("id")
                    }
                }

                val comment = connection.prepareStatement(
                    """
                    INSERT INTO comments (thread_id, author, content)
                    VALUES (?, ?, ?)
                    RETURNING id, created_at
                    """.trimIndent()
                ).use { statement ->
                    statement.setLong(1, threadId)
                    statement.setString(2, request.author)
                    statement.setString(3, request.text)
                    statement.executeQuery().use { result ->
                        check(result.next()) { "Created comment was not returned" }
                        Comment(
                            id = result.getLong("id"),
                            matchId = matchId,
                            author = request.author,
                            text = request.text,
                            createdAt = result.getTimestamp("created_at")
                                .toInstant()
                                .toString(),
                        )
                    }
                }

                connection.commit()
                comment
            } catch (e: Exception) {
                try {
                    connection.rollback()
                } catch (rollbackError: Exception) {
                    e.addSuppressed(rollbackError)
                }
                throw e
            }
        }
    }

    override suspend fun getByMatchId(//コメント一覧を返す。
        matchId: Int,
    ): List<Comment> = withContext(Dispatchers.IO) {
        DatabaseFactory.getConnection().use { connection ->
            connection.prepareStatement(//c=comments.t=threads。ここだけは後に理解。
                """
SELECT
    c.id,
    t.match_id,
    c.author,
    c.content,
    c.created_at
FROM comments AS c
INNER JOIN threads AS t
    ON c.thread_id = t.id
WHERE t.id = (
    SELECT id
    FROM threads
    WHERE match_id = ?
      AND status = 'OPEN'
    ORDER BY number DESC
    LIMIT 1
)
ORDER BY c.created_at, c.id
""".trimIndent()
            ).use { statement ->
                statement.setLong(1, matchId.toLong())

                try {
                    statement.executeQuery().use { result ->
                        buildList {
                            while (result.next()) {
                                add(
                                    Comment(
                                        id = result.getLong("id"),
                                        matchId = result.getInt("match_id"),
                                        author = result.getString("author"),
                                        text = result.getString("content"),
                                        createdAt = result
                                            .getTimestamp("created_at")
                                            .toInstant()
                                            .toString(),
                                    )
                                )
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    throw e
                }
            }
        }
    }
}