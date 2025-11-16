package com.kdongsu5509.imhere.auth.application.service.jwt

import com.kdongsu5509.imhere.auth.application.port.out.CachePort
import com.kdongsu5509.imhere.common.exception.implementation.auth.ImHereTokenExpiredException
import com.kdongsu5509.imhere.common.exception.implementation.auth.ImHereTokenInvalidException
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentCaptor
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import java.time.Duration

@ExtendWith(MockitoExtension::class)
class SelfSignedTokenProviderTest {

    @Mock
    private lateinit var jwtTokenIssuer: JwtTokenIssuer

    @Mock
    private lateinit var jwtTokenUtil: JwtTokenUtil

    @Mock
    private lateinit var cachePort: CachePort

    private lateinit var selfSignedTokenProvider: SelfSignedTokenProvider

    @BeforeEach
    fun setUp() {
        selfSignedTokenProvider = SelfSignedTokenProvider(jwtTokenIssuer, jwtTokenUtil, cachePort)
    }

    @Test
    @DisplayName("JWT 인증 토큰을 성공적으로 발급한다")
    fun issueJwtAuth_success() {
        // given
        val email = "test@example.com"
        val role = "USER"
        val accessToken = "access-token"
        val refreshToken = "refresh-token"
        val expirationDate = java.time.LocalDateTime.now().plusDays(7)

        `when`(jwtTokenIssuer.createAccessToken(email, role)).thenReturn(accessToken)
        `when`(jwtTokenIssuer.createRefreshToken(email, role)).thenReturn(refreshToken)
        `when`(jwtTokenUtil.getExpirationDateFromToken(refreshToken)).thenReturn(expirationDate)

        // when
        val result = selfSignedTokenProvider.issueJwtAuth(email, role)

        // then
        assertThat(result).isNotNull()
        assertThat(result.accessToken).isEqualTo(accessToken)
        assertThat(result.refreshToken).isEqualTo(refreshToken)

        verify(jwtTokenIssuer).createAccessToken(email, role)
        verify(jwtTokenIssuer).createRefreshToken(email, role)
        verify(jwtTokenUtil).getExpirationDateFromToken(refreshToken)
    }

    @Test
    @DisplayName("유효한 리프레시 토큰으로 JWT 토큰을 재발급한다")
    fun reissueJwtToken_validRefreshToken_success() {
        // given
        val refreshToken = "valid-refresh-token"
        val username = "test@example.com"
        val role = "ROLE_USER"
        val expirationDate = java.time.LocalDateTime.now().plusDays(1)
        val newAccessToken = "new-access-token"
        val newRefreshToken = "new-refresh-token"
        val redisKey = "refresh:$username"

        `when`(jwtTokenUtil.getUsernameFromToken(refreshToken)).thenReturn(username)
        `when`(jwtTokenUtil.getRoleFromToken(refreshToken)).thenReturn(role)
        `when`(jwtTokenUtil.validateToken(refreshToken)).thenReturn(true)
        `when`(jwtTokenUtil.getExpirationDateFromToken(refreshToken)).thenReturn(expirationDate)
        `when`(cachePort.find(redisKey)).thenReturn(refreshToken)
        `when`(jwtTokenIssuer.createAccessToken(username, role)).thenReturn(newAccessToken)
        `when`(jwtTokenIssuer.createRefreshToken(username, role)).thenReturn(newRefreshToken)
        `when`(jwtTokenUtil.getExpirationDateFromToken(newRefreshToken)).thenReturn(expirationDate)

        // when
        val result = selfSignedTokenProvider.reissueJwtToken(refreshToken)

        // then
        assertThat(result).isNotNull()
        assertThat(result.accessToken).isEqualTo(newAccessToken)
        assertThat(result.refreshToken).isEqualTo(newRefreshToken)

        verify(jwtTokenUtil).getUsernameFromToken(refreshToken)
        verify(jwtTokenUtil).getRoleFromToken(refreshToken)
        verify(jwtTokenUtil).validateToken(refreshToken)
        verify(jwtTokenUtil).getExpirationDateFromToken(refreshToken)
        verify(cachePort).find(redisKey)
        verify(jwtTokenIssuer).createAccessToken(username, role)
        verify(jwtTokenIssuer).createRefreshToken(username, role)
        verify(jwtTokenUtil).getExpirationDateFromToken(newRefreshToken)

        val keyCaptor: ArgumentCaptor<String> = ArgumentCaptor.forClass(String::class.java)
        val valueCaptor: ArgumentCaptor<String> = ArgumentCaptor.forClass(String::class.java)
        val durationCaptor: ArgumentCaptor<Duration> = ArgumentCaptor.forClass(Duration::class.java)
        verify(cachePort).save(capture(keyCaptor), capture(valueCaptor), capture(durationCaptor))
        assertThat(keyCaptor.value).isEqualTo("refresh:$username")
        assertThat(valueCaptor.value).isEqualTo(newRefreshToken)
        assertThat(durationCaptor.value).isNotNull()
    }

    fun <T> capture(captor: ArgumentCaptor<T>): T = captor.capture()


    @Test
    @DisplayName("유효하지 않은 리프레시 토큰으로 재발급 시 예외가 발생한다")
    fun reissueJwtToken_invalidRefreshToken_throwsException() {
        // given
        val invalidRefreshToken = "invalid-refresh-token"
        val username = "test@example.com"
        val role = "ROLE_USER"

        `when`(jwtTokenUtil.getUsernameFromToken(invalidRefreshToken)).thenReturn(username)
        `when`(jwtTokenUtil.getRoleFromToken(invalidRefreshToken)).thenReturn(role)
        `when`(jwtTokenUtil.validateToken(invalidRefreshToken)).thenReturn(false)

        // when & then
        assertThrows< ImHereTokenInvalidException> {
            selfSignedTokenProvider.reissueJwtToken(invalidRefreshToken)
        }.also { exception ->
            assertThat(exception.message).isEqualTo("잘못된 토큰입니다")
        }

        verify(jwtTokenUtil).validateToken(invalidRefreshToken)
    }

    @Test
    @DisplayName("만료된 리프레시 토큰으로 재발급 시 예외가 발생한다")
    fun reissueJwtToken_expiredRefreshToken_throwsException() {
        // given
        val expiredRefreshToken = "expired-refresh-token"
        val username = "test@example.com"
        val role = "ROLE_USER"
        val expiredDate = java.time.LocalDateTime.now().minusDays(1)

        `when`(jwtTokenUtil.getUsernameFromToken(expiredRefreshToken)).thenReturn(username)
        `when`(jwtTokenUtil.getRoleFromToken(expiredRefreshToken)).thenReturn(role)
        `when`(jwtTokenUtil.validateToken(expiredRefreshToken)).thenReturn(true)
        `when`(jwtTokenUtil.getExpirationDateFromToken(expiredRefreshToken)).thenReturn(expiredDate)

        // when & then
        assertThrows<ImHereTokenExpiredException> {
            selfSignedTokenProvider.reissueJwtToken(expiredRefreshToken)
        }.also { exception ->
            assertThat(exception.message).isEqualTo("만료된 토큰입니다")
        }

        verify(jwtTokenUtil).getExpirationDateFromToken(expiredRefreshToken)
    }

    @Test
    @DisplayName("Redis에 저장된 토큰과 일치하지 않는 리프레시 토큰으로 재발급 시 예외가 발생한다")
    fun reissueJwtToken_mismatchedRefreshToken_throwsException() {
        // given
        val refreshToken = "refresh-token"
        val differentTokenInRedis = "different-refresh-token"
        val username = "test@example.com"
        val role = "ROLE_USER"
        val expirationDate = java.time.LocalDateTime.now().plusDays(1)
        val redisKey = "refresh:$username"

        `when`(jwtTokenUtil.getUsernameFromToken(refreshToken)).thenReturn(username)
        `when`(jwtTokenUtil.getRoleFromToken(refreshToken)).thenReturn(role)
        `when`(jwtTokenUtil.validateToken(refreshToken)).thenReturn(true)
        `when`(jwtTokenUtil.getExpirationDateFromToken(refreshToken)).thenReturn(expirationDate)
        `when`(cachePort.find(redisKey)).thenReturn(differentTokenInRedis)

        // when & then
        assertThrows<IllegalArgumentException> {
            selfSignedTokenProvider.reissueJwtToken(refreshToken)
        }.also { exception ->
            assertThat(exception.message).isEqualTo("일치하지 않는 리프레시 토큰")
        }

        verify(cachePort).find(redisKey)
    }

    @Test
    @DisplayName("Redis에 리프레시 토큰이 없을 때 재발급 시 예외가 발생한다")
    fun reissueJwtToken_noTokenInRedis_throwsException() {
        // given
        val refreshToken = "refresh-token"
        val username = "test@example.com"
        val role = "ROLE_USER"
        val expirationDate = java.time.LocalDateTime.now().plusDays(1)
        val redisKey = "refresh:$username"

        `when`(jwtTokenUtil.getUsernameFromToken(refreshToken)).thenReturn(username)
        `when`(jwtTokenUtil.getRoleFromToken(refreshToken)).thenReturn(role)
        `when`(jwtTokenUtil.validateToken(refreshToken)).thenReturn(true)
        `when`(jwtTokenUtil.getExpirationDateFromToken(refreshToken)).thenReturn(expirationDate)
        `when`(cachePort.find(redisKey)).thenReturn(null)

        // when & then
        assertThrows<IllegalArgumentException> {
            selfSignedTokenProvider.reissueJwtToken(refreshToken)
        }.also { exception ->
            assertThat(exception.message).isEqualTo("일치하지 않는 리프레시 토큰")
        }

        verify(cachePort).find(redisKey)
    }

    @Test
    @DisplayName("재발급 시 토큰에서 추출한 role을 그대로 전달한다")
    fun reissueJwtToken_passesRoleAsIs() {
        // given
        val refreshToken = "valid-refresh-token"
        val username = "test@example.com"
        val role = "ROLE_USER"
        val expirationDate = java.time.LocalDateTime.now().plusDays(1)
        val newAccessToken = "new-access-token"
        val newRefreshToken = "new-refresh-token"
        val redisKey = "refresh:$username"

        `when`(jwtTokenUtil.getUsernameFromToken(refreshToken)).thenReturn(username)
        `when`(jwtTokenUtil.getRoleFromToken(refreshToken)).thenReturn(role)
        `when`(jwtTokenUtil.validateToken(refreshToken)).thenReturn(true)
        `when`(jwtTokenUtil.getExpirationDateFromToken(refreshToken)).thenReturn(expirationDate)
        `when`(cachePort.find(redisKey)).thenReturn(refreshToken)
        `when`(jwtTokenIssuer.createAccessToken(username, role)).thenReturn(newAccessToken)
        `when`(jwtTokenIssuer.createRefreshToken(username, role)).thenReturn(newRefreshToken)
        `when`(jwtTokenUtil.getExpirationDateFromToken(newRefreshToken)).thenReturn(expirationDate)

        // when
        val result = selfSignedTokenProvider.reissueJwtToken(refreshToken)

        // then
        assertThat(result).isNotNull()
        assertThat(result.accessToken).isEqualTo(newAccessToken)
        assertThat(result.refreshToken).isEqualTo(newRefreshToken)
        verify(jwtTokenIssuer).createAccessToken(username, role)
        verify(jwtTokenIssuer).createRefreshToken(username, role)
        verify(jwtTokenUtil).getExpirationDateFromToken(newRefreshToken)
//
//        val keyCaptor: ArgumentCaptor<String> = ArgumentCaptor.forClass(String::class.java)
//        val valueCaptor: ArgumentCaptor<String> = ArgumentCaptor.forClass(String::class.java)
//        val durationCaptor: ArgumentCaptor<Duration> = ArgumentCaptor.forClass(Duration::class.java)
//        verify(cachePort).save(keyCaptor.capture(), valueCaptor.capture(), durationCaptor.capture())
//        assertThat(keyCaptor.value).isEqualTo("refresh:$username")
//        assertThat(valueCaptor.value).isEqualTo(newRefreshToken)
//        assertThat(durationCaptor.value).isNotNull()
    }
}

