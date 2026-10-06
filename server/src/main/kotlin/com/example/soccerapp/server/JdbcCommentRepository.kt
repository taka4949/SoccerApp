package com.example.soccerapp.server

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class JdbcCommentRepository : CommentRepository {


//スレッド管理はKtor側
    //ResultSetは、0行目参照。result.next()で1行目参照。



    override suspend fun create(//コメント1件保存用
        matchId: Int,
        request: CreateCommentRequest,
    ): Comment = withContext(Dispatchers.IO) {//sqlを送ってから待ち時間が発生→待機時間が発生する処理向けの処理スレッド


        DatabaseFactory.getConnection().use { connection ->//useは、｛｝終了後、connection.close()
            connection.autoCommit = false//処理を一元化→どこか失敗したら失敗扱い
            try {

                connection.prepareStatement(//ポスグレに送る準備。↓このSQL意味は同スレッドの並列処理×
                    "SELECT pg_advisory_xact_lock(?)"
                ).use { statement ->
                    statement.setLong(1, matchId.toLong())//SQL内の1番目の？→matchId
                    statement.executeQuery().use { result -> result.next() }//ポスグレへ送る
                }


                val existingThreadId = connection.prepareStatement(//threadID取得。
                    """
                SELECT id
                FROM threads
                WHERE match_id = ?
                  AND status = 'OPEN'
                ORDER BY number DESC
                LIMIT 1
                """.trimIndent()//DESC＝大きい順→最新のスレッドを取得。
                ).use { statement ->
                    statement.setLong(1, matchId.toLong())//?=matchId
                    statement.executeQuery().use { result ->//resultがSQLの結果
                        if (result.next()) result.getLong("id") else null//取得→existingThreadIdへ。
                    }
                }



                val threadId = existingThreadId ?: connection.prepareStatement(//既存のスレッドID利用orなければ新しいスレッドID作成↓
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



                val comment = connection.prepareStatement(//ここでコメントテーブルにコメント追加+コメント取得Comment()
                    """
                INSERT INTO comments (
                    thread_id,
                    author,
                    content
                )
                VALUES (?, ?, ?)
                RETURNING id, created_at
                """.trimIndent()
                ).use { statement ->
                    statement.setLong(1, threadId)
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

                connection.commit()
                comment//return


            } catch (e: Exception) {

                try {
                    connection.rollback()//途中まで行ったDB変更を取り消す
                } catch (rollbackError: Exception) {
                    e.addSuppressed(rollbackError)
                }
                throw e

            }
        }
    }







    override suspend fun getByMatchId(//コメント一覧を返す用。
        matchId: Int,
    ): List<Comment> = withContext(Dispatchers.IO) {
        DatabaseFactory.getConnection().use { connection ->
            connection.prepareStatement(//INNER JOINはコメントテーブルにthreadsテーブルの情報も紐づける。onは同じ行同士を結ぶ
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
""".trimIndent()//↑これでコメント欄を整理（時刻が古い順、id小さい順など）
            ).use { statement ->
                statement.setLong(1, matchId.toLong())

                try {
                    statement.executeQuery().use { result ->
                        buildList {
                            while (result.next()) {//コメント存在する限りリストに追加。
                                add(
                                    Comment(
                                        id = result.getLong("id"),//c.id
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
                    e.printStackTrace()//例外の詳細をサーバーのログへ
                    throw e
                }
            }
        }
    }
}

