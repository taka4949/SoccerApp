
package com.example.soccerapp.server

import de.mkammerer.argon2.Argon2Factory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.sql.SQLException
import java.util.Locale


class EmailAlreadyRegisteredException(//エラー内容を個別に用意
    cause: Throwable
) : RuntimeException("Email already registered", cause)//classだから継承

class JdbcAuthRepository {//DB操作

    suspend fun register(
        request: RegisterRequest
    ): RegisterResponse = withContext(Dispatchers.IO) {

        val email = request.email
            .trim()
            .lowercase(Locale.ROOT)//変換ルール指定

        val argon2 = Argon2Factory.create(
            Argon2Factory.Argon2Types.ARGON2id
        )
        val passwordChars = request.password.toCharArray()

        val passwordHash = try {//ハッシュ化
            argon2.hash(
                3,
                65536,
                1,
                passwordChars
            )
        } finally {//tryでもcatchでも実行される。
            argon2.wipeArray(passwordChars)
        }




        try {
            DatabaseFactory.getConnection().use { connection ->

                connection.prepareStatement(//ユーザーテーブルへ追加
                    """
                    INSERT INTO users (email, password_hash)
                    VALUES (?, ?)
                    RETURNING id, email
                    """.trimIndent()
                ).use { statement ->

                    statement.setString(1, email)
                    statement.setString(2, passwordHash)

                    statement.executeQuery().use { result ->

                        check(result.next())

                        RegisterResponse(
                            id = result.getLong("id"),
                            email = result.getString("email")
                        )
                    }
                }
            }



        } catch (e: SQLException) {
            if (e.sqlState == "23505") {//23505 = unique_violation（一意性違反）
                throw EmailAlreadyRegisteredException(e)
            }
            throw e
        }
    }
}


