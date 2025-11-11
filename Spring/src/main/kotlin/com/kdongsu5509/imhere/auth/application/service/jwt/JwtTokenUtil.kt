package com.kdongsu5509.imhere.auth.application.service.jwt

import io.jsonwebtoken.*
import io.jsonwebtoken.security.Keys
import io.jsonwebtoken.security.SecurityException
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets
import java.time.LocalDateTime
import java.time.ZoneId
import javax.crypto.SecretKey

@Component
class JwtTokenUtil(private val jwtProperties: JwtProperties) {

    private val zoneID: ZoneId = ZoneId.systemDefault()
    private val secretKey: SecretKey by lazy {
        val keyBytes = jwtProperties.secret.toByteArray(StandardCharsets.UTF_8)
        Keys.hmacShaKeyFor(keyBytes)
    }

    fun validateToken(token: String): Boolean {
        try {
            parseClaims(token)
            return true
        } catch (e: SecurityException) {
            print("잘못된 JWT 서명입니다: ${e.message}")
        } catch (e: MalformedJwtException) {
            print("잘못된 JWT 서명입니다: ${e.message}")
        } catch (e: ExpiredJwtException) {
            print("만료된 JWT 서명입니다: ${e.message}")
        } catch (e: UnsupportedJwtException) {
            print("지원되지 않는 JWT 서명입니다: ${e.message}")
        } catch (e: IllegalArgumentException) {
            print("JWT 토큰이 잘못되었습니다 ${e.message}")
        }
        return false
    }

    fun getJwtIdFromToken(token: String): String {
        return parseClaims(token).id
    }

    fun getUsernameFromToken(token: String): String {
        return parseClaims(token)["username"] as String
    }

    fun getRoleFromToken(token: String): String {
        return parseClaims(token)["role"] as String
    }

    fun getExpirationDateFromToken(token: String): LocalDateTime {
        val expiration = parseClaims(token).expiration
        return LocalDateTime.ofInstant(expiration.toInstant(), zoneID)
    }

    private fun parseClaims(token: String): Claims {
        return Jwts.parserBuilder()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJws(token)
            .body
    }
}