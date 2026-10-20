package com.example.soccerapp.server

import de.mkammerer.argon2.Argon2Factory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.security.MessageDigest
import java.security.SecureRandom
import java.sql.Timestamp
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.Base64
import java.util.HexFormat
import java.util.Locale

interface SessionService {
    suspend fun login(email: String, password: String): SessionResponse?
    suspend fun resolve(token: String?): Long?
    suspend fun logout(token: String?)
}

fun extractBearerToken(header: String?): String? {
    val value = header?.trim() ?: return null
    if (!value.startsWith("Bearer ", ignoreCase = true)) return null
    val token = value.substring(7).trim()
    return token.takeIf { it.length == 43 && it.all { c ->
        c in 'A'..'Z' || c in 'a'..'z' || c in '0'..'9' || c == '-' || c == '_'
    } }
}

private fun digestToken(token: String): String =
    HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(token.toByteArray(Charsets.UTF_8)))

class JdbcSessionService : SessionService {
    private val random = SecureRandom()

    override suspend fun login(email: String, password: String): SessionResponse? = withContext(Dispatchers.IO) {
        val normalized = email.trim().lowercase(Locale.ROOT)
        val row = DatabaseFactory.getConnection().use { connection ->
            connection.prepareStatement("SELECT id, password_hash FROM users WHERE email = ?").use { statement ->
                statement.setString(1, normalized)
                statement.executeQuery().use { result ->
                    if (result.next()) result.getLong("id") to result.getString("password_hash") else null
                }
            }
        } ?: return@withContext null

        val passwordChars = password.toCharArray()
        val argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id)
        val valid = try {
            argon2.verify(row.second, passwordChars)
        } finally {
            argon2.wipeArray(passwordChars)
        }
        if (!valid) return@withContext null

        val bytes = ByteArray(32)
        random.nextBytes(bytes)
        val token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes)
        bytes.fill(0)
        val expiry = Instant.now().plus(30, ChronoUnit.DAYS)
        DatabaseFactory.getConnection().use { connection ->
            connection.prepareStatement(
                "INSERT INTO user_sessions (token_hash, user_id, expires_at) VALUES (?, ?, ?)"
            ).use { statement ->
                statement.setString(1, digestToken(token))
                statement.setLong(2, row.first)
                statement.setTimestamp(3, Timestamp.from(expiry))
                statement.executeUpdate()
            }
        }
        SessionResponse(accessToken = token, expiresAt = expiry.toString(), userId = row.first)
    }

    override suspend fun resolve(token: String?): Long? = withContext(Dispatchers.IO) {
        if (token == null || extractBearerToken("Bearer $token") == null) return@withContext null
        DatabaseFactory.getConnection().use { connection ->
            connection.prepareStatement(
                "SELECT user_id FROM user_sessions WHERE token_hash = ? AND expires_at > CURRENT_TIMESTAMP"
            ).use { statement ->
                statement.setString(1, digestToken(token))
                statement.executeQuery().use { result ->
                    if (result.next()) result.getLong("user_id") else null
                }
            }
        }
    }

    override suspend fun logout(token: String?) {
        if (token == null || extractBearerToken("Bearer $token") == null) return
        withContext(Dispatchers.IO) {
            DatabaseFactory.getConnection().use { connection ->
                connection.prepareStatement("DELETE FROM user_sessions WHERE token_hash = ?").use { statement ->
                    statement.setString(1, digestToken(token))
                    statement.executeUpdate()
                }
            }
        }
    }
}
