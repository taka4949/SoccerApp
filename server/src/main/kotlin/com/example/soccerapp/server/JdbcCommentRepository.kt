package com.example.soccerapp.server

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext



//NEWっていう部分は新規部分、のちに理解。9/22→
//既存のIDがあれば使う。null なら新しいスレッドを作り、そのIDを使う、という変更ではある。9/22
class JdbcCommentRepository : CommentRepository {

    override suspend fun create(//保存用。この試合にコメントからKtorへ→matchID→結びついているthreadID検索→それを付帯したコメントをテーブルに保存。
        matchId: Int,
        request: CreateCommentRequest,
    ): Comment = withContext(Dispatchers.IO) {//sqlを送ってから待ち時間が発生する。待機時間が発生する処理向けの処理スレッド
        DatabaseFactory.getConnection().use { connection ->
            connection.autoCommit = false // NEW
            try { // NEW
                //
                connection.prepareStatement(
                    "SELECT pg_advisory_xact_lock(?)"
                ).use { statement ->
                    statement.setLong(1, matchId.toLong())
                    statement.executeQuery().use { result -> result.next() }
                }
                // NEW END

                // NEW: the lookup may return null
                val existingThreadId = connection.prepareStatement(//threadID取得。androidはこれを知らない。スレッド管理はKtor側。
                    """
                SELECT id
                FROM threads
                WHERE match_id = ?
                  AND status = 'OPEN'
                ORDER BY number DESC
                LIMIT 1
                """.trimIndent()//DESC＝大きい順→最新のスレッドを取得したいから。つまりはコメント数多いもの
                ).use { statement ->
                    statement.setLong(1, matchId.toLong())//上のSQLへセットする！

                    statement.executeQuery().use { result ->//PostgreSQLへ送る実行、resultは返り値
                        if (result.next()) result.getLong("id") else null // NEW
                    }
                }

                // NEW START: create a thread only when the lookup returned null
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
                // NEW END

                // NEW: hold the created comment until commit
                val comment = connection.prepareStatement(//コメントの保存
                    """
                INSERT INTO comments (
                    thread_id,
                    author,
                    content
                )
                VALUES (?, ?, ?)
                RETURNING id, created_at
                """.trimIndent()//ここでコメントテーブルにコメント追加。VALUESは指定した列へ入れる。RETURNINGはkotlin側へその値を返す。
                ).use { statement ->
                    statement.setLong(1, threadId)//VALUESの1つ目の内容、以下2～3つ目。
                    statement.setString(2, request.author)
                    statement.setString(3, request.text)

                    statement.executeQuery().use { result ->
                        check(result.next()) {
                            "Created comment was not returned"
                        }

                        Comment(//android側に返すために、kotlinデータへ変換する。
                            id = result.getLong("id"),
                            matchId = matchId,
                            author = request.author,
                            text = request.text,
                            createdAt = result
                                .getTimestamp("created_at")//日時、
                                .toInstant()
                                .toString(),
                        )
                    }
                }

                connection.commit() // NEW
                comment // NEW
            } catch (e: Exception) {
                // NEW START: discard the thread if comment insertion fails
                try {
                    connection.rollback()
                } catch (rollbackError: Exception) {
                    e.addSuppressed(rollbackError)
                }
                throw e
                // NEW END
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