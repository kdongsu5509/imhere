package com.kdongsu5509.imhere.auth.application.service.jwt

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import javax.crypto.SecretKey

@Component
class JwtTokenIssuer(private val jwtProperties: JwtProperties) {

    private val zoneID: ZoneId = ZoneId.systemDefault()
    private val secretKey: SecretKey by lazy {
        val keyBytes = jwtProperties.secret.toByteArray(StandardCharsets.UTF_8)
        Keys.hmacShaKeyFor(keyBytes)
    }

    fun createAccessToken(userName: String, role: String): String {
        val expiredTime = LocalDateTime.now().plusMinutes(jwtProperties.accessExpirationMinutes)
        return Jwts.builder()
            .setId(UUID.randomUUID().toString())
            .claim("category", "access")         // claim 메서드는 동일
            .claim("username", userName)
            .claim("role", "ROLE_${role}")
            .setIssuedAt(Date.from(Instant.now()))
            .setExpiration(Date.from(expiredTime.atZone(zoneID).toInstant()))
            .signWith(secretKey)
            .compact()
    }

    fun createRefreshToken(userName: String, role: String): String {
        val expiredTime = LocalDateTime.now().plusDays(jwtProperties.refreshExpirationDays);
        return Jwts.builder()
            .setId(UUID.randomUUID().toString())
            .claim("category", "refresh")         // claim 메서드는 동일
            .claim("username", userName)
            .claim("role", "ROLE_${role}")
            .setIssuedAt(Date.from(Instant.now()))
            .setExpiration(Date.from(expiredTime.atZone(zoneID).toInstant()))
            .signWith(secretKey)
            .compact()
    }

}