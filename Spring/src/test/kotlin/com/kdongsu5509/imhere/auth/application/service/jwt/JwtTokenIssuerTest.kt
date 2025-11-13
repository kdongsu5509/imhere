package com.kdongsu5509.imhere.auth.application.service.jwt

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.nio.charset.StandardCharsets
import java.time.LocalDateTime
import java.time.ZoneId

class JwtTokenIssuerTest {

    private lateinit var jwtTokenIssuer: JwtTokenIssuer
    private lateinit var jwtProperties: JwtProperties

    @BeforeEach
    fun setUp() {
        jwtProperties = JwtProperties(
            secret = "testSecretKeyForJwtTokenIssuerTest12345678901234567890",
            accessExpirationMinutes = 30,
            refreshExpirationDays = 7
        )
        jwtTokenIssuer = JwtTokenIssuer(jwtProperties)
    }

    @Test
    @DisplayName("AccessToken을 성공적으로 생성한다")
    fun createAccessToken_success() {
        // given
        val userName = "test@example.com"
        val role = "USER"

        // when
        val accessToken = jwtTokenIssuer.createAccessToken(userName, role)

        // then
        assertThat(accessToken).isNotEmpty()
        
        // 토큰 파싱하여 검증
        val secretKey = Keys.hmacShaKeyFor(jwtProperties.secret.toByteArray(StandardCharsets.UTF_8))
        val claims = Jwts.parserBuilder()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJws(accessToken)
            .body

        assertThat(claims["username"]).isEqualTo(userName)
        assertThat(claims["role"]).isEqualTo("ROLE_$role")
        assertThat(claims["category"]).isEqualTo("access")
        assertThat(claims.id).isNotNull()
        assertThat(claims.issuedAt).isNotNull()
        assertThat(claims.expiration).isNotNull()
        
        // 만료 시간이 현재 시간보다 미래인지 확인
        val expirationTime = LocalDateTime.ofInstant(claims.expiration.toInstant(), ZoneId.systemDefault())
        assertThat(expirationTime).isAfter(LocalDateTime.now())
    }

    @Test
    @DisplayName("RefreshToken을 성공적으로 생성한다")
    fun createRefreshToken_success() {
        // given
        val userName = "test@example.com"
        val role = "USER"

        // when
        val refreshToken = jwtTokenIssuer.createRefreshToken(userName, role)

        // then
        assertThat(refreshToken).isNotEmpty()
        
        // 토큰 파싱하여 검증
        val secretKey = Keys.hmacShaKeyFor(jwtProperties.secret.toByteArray(StandardCharsets.UTF_8))
        val claims = Jwts.parserBuilder()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJws(refreshToken)
            .body

        assertThat(claims["username"]).isEqualTo(userName)
        assertThat(claims["role"]).isEqualTo("ROLE_$role")
        assertThat(claims["category"]).isEqualTo("refresh")
        assertThat(claims.id).isNotNull()
        assertThat(claims.issuedAt).isNotNull()
        assertThat(claims.expiration).isNotNull()
        
        // 만료 시간이 현재 시간보다 미래인지 확인
        val expirationTime = LocalDateTime.ofInstant(claims.expiration.toInstant(), ZoneId.systemDefault())
        assertThat(expirationTime).isAfter(LocalDateTime.now())
    }

    @Test
    @DisplayName("AccessToken과 RefreshToken의 만료 시간이 다르다")
    fun accessTokenAndRefreshTokenHaveDifferentExpiration() {
        // given
        val userName = "test@example.com"
        val role = "USER"

        // when
        val accessToken = jwtTokenIssuer.createAccessToken(userName, role)
        val refreshToken = jwtTokenIssuer.createRefreshToken(userName, role)

        // then
        val secretKey = Keys.hmacShaKeyFor(jwtProperties.secret.toByteArray(StandardCharsets.UTF_8))
        
        val accessClaims = Jwts.parserBuilder()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJws(accessToken)
            .body

        val refreshClaims = Jwts.parserBuilder()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJws(refreshToken)
            .body

        val accessExpiration = LocalDateTime.ofInstant(accessClaims.expiration.toInstant(), ZoneId.systemDefault())
        val refreshExpiration = LocalDateTime.ofInstant(refreshClaims.expiration.toInstant(), ZoneId.systemDefault())

        // RefreshToken이 AccessToken보다 더 오래 유효해야 함
        assertThat(refreshExpiration).isAfter(accessExpiration)
    }

    @Test
    @DisplayName("같은 사용자로 생성한 토큰도 매번 다른 ID를 가진다")
    fun tokensHaveDifferentIds() {
        // given
        val userName = "test@example.com"
        val role = "USER"

        // when
        val token1 = jwtTokenIssuer.createAccessToken(userName, role)
        val token2 = jwtTokenIssuer.createAccessToken(userName, role)

        // then
        val secretKey = Keys.hmacShaKeyFor(jwtProperties.secret.toByteArray(StandardCharsets.UTF_8))
        
        val claims1 = Jwts.parserBuilder()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJws(token1)
            .body

        val claims2 = Jwts.parserBuilder()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJws(token2)
            .body

        assertThat(claims1.id).isNotEqualTo(claims2.id)
    }
}

