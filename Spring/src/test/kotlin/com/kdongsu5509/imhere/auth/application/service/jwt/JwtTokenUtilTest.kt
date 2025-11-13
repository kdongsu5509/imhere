package com.kdongsu5509.imhere.auth.application.service.jwt

import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import java.nio.charset.StandardCharsets
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

@ExtendWith(MockitoExtension::class)
class JwtTokenUtilTest {

    private lateinit var jwtTokenUtil: JwtTokenUtil
    private lateinit var jwtProperties: JwtProperties
    private lateinit var secretKey: javax.crypto.SecretKey

    @BeforeEach
    fun setUp() {
        jwtProperties = JwtProperties(
            secret = "testSecretKeyForJwtTokenUtilTest12345678901234567890",
            accessExpirationMinutes = 30,
            refreshExpirationDays = 7
        )
        jwtTokenUtil = JwtTokenUtil(jwtProperties)
        secretKey = Keys.hmacShaKeyFor(jwtProperties.secret.toByteArray(StandardCharsets.UTF_8))
    }

    private fun createValidToken(username: String, role: String, expirationMinutes: Long): String {
        val expiredTime = LocalDateTime.now().plusMinutes(expirationMinutes)
        return Jwts.builder()
            .setId(UUID.randomUUID().toString())
            .claim("category", "access")
            .claim("username", username)
            .claim("role", role)
            .setIssuedAt(Date.from(Instant.now()))
            .setExpiration(Date.from(expiredTime.atZone(ZoneId.systemDefault()).toInstant()))
            .signWith(secretKey)
            .compact()
    }

    @Test
    @DisplayName("유효한 토큰을 검증하면 true를 반환한다")
    fun validateToken_validToken_returnsTrue() {
        // given
        val token = createValidToken("test@example.com", "ROLE_USER", 30)

        // when
        val result = jwtTokenUtil.validateToken(token)

        // then
        assertThat(result).isTrue()
    }

    @Test
    @DisplayName("만료된 토큰을 검증하면 false를 반환한다")
    fun validateToken_expiredToken_returnsFalse() {
        // given
        val expiredTime = LocalDateTime.now().minusMinutes(1)
        val token = Jwts.builder()
            .setId(UUID.randomUUID().toString())
            .claim("username", "test@example.com")
            .claim("role", "ROLE_USER")
            .setIssuedAt(Date.from(Instant.now().minusSeconds(3600)))
            .setExpiration(Date.from(expiredTime.atZone(ZoneId.systemDefault()).toInstant()))
            .signWith(secretKey)
            .compact()

        // when
        val result = jwtTokenUtil.validateToken(token)

        // then
        assertThat(result).isFalse()
    }

    @Test
    @DisplayName("잘못된 서명의 토큰을 검증하면 false를 반환한다")
    fun validateToken_invalidSignature_returnsFalse() {
        // given
        val wrongSecretKey =
            Keys.hmacShaKeyFor("wrongSecretKey123456789012345678901234567890".toByteArray(StandardCharsets.UTF_8))
        val token = Jwts.builder()
            .setId(UUID.randomUUID().toString())
            .claim("username", "test@example.com")
            .claim("role", "ROLE_USER")
            .setIssuedAt(Date.from(Instant.now()))
            .setExpiration(Date.from(LocalDateTime.now().plusMinutes(30).atZone(ZoneId.systemDefault()).toInstant()))
            .signWith(wrongSecretKey)
            .compact()

        // when
        val result = jwtTokenUtil.validateToken(token)

        // then
        assertThat(result).isFalse()
    }

    @Test
    @DisplayName("잘못된 형식의 토큰을 검증하면 false를 반환한다")
    fun validateToken_malformedToken_returnsFalse() {
        // given
        val malformedToken = "invalid.token.format"

        // when
        val result = jwtTokenUtil.validateToken(malformedToken)

        // then
        assertThat(result).isFalse()
    }

    @Test
    @DisplayName("빈 문자열 토큰을 검증하면 false를 반환한다")
    fun validateToken_emptyToken_returnsFalse() {
        // given
        val emptyToken = ""

        // when
        val result = jwtTokenUtil.validateToken(emptyToken)

        // then
        assertThat(result).isFalse()
    }

    @Test
    @DisplayName("토큰에서 username을 성공적으로 추출한다")
    fun getUsernameFromToken_success() {
        // given
        val expectedUsername = "test@example.com"
        val token = createValidToken(expectedUsername, "ROLE_USER", 30)

        // when
        val result = jwtTokenUtil.getUsernameFromToken(token)

        // then
        assertThat(result).isEqualTo(expectedUsername)
    }

    @Test
    @DisplayName("토큰에서 role을 성공적으로 추출한다")
    fun getRoleFromToken_success() {
        // given
        val expectedRole = "ROLE_ADMIN"
        val token = createValidToken("test@example.com", expectedRole, 30)

        // when
        val result = jwtTokenUtil.getRoleFromToken(token)

        // then
        assertThat(result).isEqualTo(expectedRole)
    }

    @Test
    @DisplayName("토큰에서 JWT ID를 성공적으로 추출한다")
    fun getJwtIdFromToken_success() {
        // given
        val token = createValidToken("test@example.com", "ROLE_USER", 30)

        // when
        val result = jwtTokenUtil.getJwtIdFromToken(token)

        // then
        assertThat(result).isNotEmpty()
    }

    @Test
    @DisplayName("토큰에서 만료 시간을 성공적으로 추출한다")
    fun getExpirationDateFromToken_success() {
        // given
        val expectedExpiration = LocalDateTime.now().plusMinutes(30)
        val token = createValidToken("test@example.com", "ROLE_USER", 30)

        // when
        val result = jwtTokenUtil.getExpirationDateFromToken(token)

        // then
        assertThat(result).isAfter(LocalDateTime.now())
        // 약간의 시간 차이를 고려하여 1분 이내의 차이인지 확인
        assertThat(result).isBefore(expectedExpiration.plusMinutes(1))
        assertThat(result).isAfter(expectedExpiration.minusMinutes(1))
    }

    @Test
    @DisplayName("유효하지 않은 토큰에서 username을 추출하면 예외가 발생한다")
    fun getUsernameFromToken_invalidToken_throwsException() {
        // given
        val invalidToken = "invalid.token"

        // when & then
        assertThatThrownBy {
            jwtTokenUtil.getUsernameFromToken(invalidToken)
        }
    }

    @Test
    @DisplayName("유효하지 않은 토큰에서 role을 추출하면 예외가 발생한다")
    fun getRoleFromToken_invalidToken_throwsException() {
        // given
        val invalidToken = "invalid.token"

        // when & then
        assertThrows<JwtException> {
            jwtTokenUtil.getRoleFromToken(invalidToken)
        }
    }

    @Test
    @DisplayName("유효하지 않은 토큰에서 만료 시간을 추출하면 예외가 발생한다")
    fun getExpirationDateFromToken_invalidToken_throwsException() {
        // given
        val invalidToken = "invalid.token"

        // when & then
        assertThrows<JwtException> {
            jwtTokenUtil.getExpirationDateFromToken(invalidToken)
        }
    }
}

