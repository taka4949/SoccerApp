package com.example.soccerapp.server

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class JdbcCommentRepository : CommentRepository {

    override suspend fun create(//この試合にコメント（android)からKtorへ→matchID→結びついているthreadID検索→それを付帯したコメントをテーブルに保存。
        matchId: Int,
        request: CreateCommentRequest,
    ): Comment = withContext(Dispatchers.IO) {//sqlを送ってから待ち時間が発生する。待機時間が発生する処理向けの処理スレッド
        DatabaseFactory.getConnection().use { connection ->
            val threadId = connection.prepareStatement(//threadID取得。androidはこれを知らない。スレッド管理はKtor側。
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
                    if (!result.next()) {
                        error(
                            "Open thread not found for matchId=$matchId"
                        )
                    }

                    result.getLong("id")
                }
            }

            connection.prepareStatement(//コメントの保存
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
            }
        }
    }
}