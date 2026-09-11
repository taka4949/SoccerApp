package com.example.soccerapp.server

import java.sql.Connection
import java.sql.DriverManager

object DatabaseFactory {//postgreSQLへ接続するために必要なもの
    private const val URL =
        "jdbc:postgresql://localhost:5432/soccer_app"

    private const val USER =
        "soccer_app_user"

    private val PASSWORD =
        System.getenv("SOCCER_DB_PASSWORD")
            ?: error("SOCCER_DB_PASSWORD is not set")


    fun getConnection(): Connection {//ここでpostgreSQLへ接続。sqlを送ることが可能。これがこのファイルの目的。
        return DriverManager.getConnection(//このファイルは接続だけを担当している。
            URL,
            USER,
            PASSWORD,
        )
    }



    fun verifyConnection() {
        getConnection().use { connection ->
            connection.prepareStatement("SELECT 1").use { statement ->
                statement.executeQuery().use { result ->
                    result.next()
                    println(
                        "PostgreSQL connection verified: ${result.getInt(1)}"
                    )
                }
            }
        }
    }
}


//connectionは、postgreSQLへsqlを送るために必要。


//実行前
//
//カーソル
//   ↓
//［行の手前］
//┌───┐
//│ 1 │
//└───|


//next()実行後
//
//┌───┐
//│ 1 │ ← カーソル
//└───┘