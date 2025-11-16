package com.kdongsu5509.imhere.auth.application.service.jwt

import com.kdongsu5509.imhere.auth.application.dto.SelfSignedJWT
import com.kdongsu5509.imhere.auth.application.port.out.CachePort
import com.kdongsu5509.imhere.common.exception.implementation.auth.ImHereTokenExpiredException
import com.kdongsu5509.imhere.common.exception.implementation.auth.ImHereTokenInvalidException
import org.springframework.stereotype.Component
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

@Component
class SelfSignedTokenProvider(
    private val jwtToeknIssuer: JwtTokenIssuer,
    private val jwtTokenUtil: JwtTokenUtil,
    private val cachePort: CachePort,
) : JwtTokenProvider {
    override fun issueJwtAuth(email: String, role: String): SelfSignedJWT {
        val accessToken = jwtToeknIssuer.createAccessToken(email, role)
        val refreshToken = jwtToeknIssuer.createRefreshToken(email, role)

        val expiredDateTime = jwtTokenUtil.getExpirationDateFromToken(refreshToken).atZone(ZoneId.systemDefault())
        val duration: Duration = Duration.between(Instant.now(), expiredDateTime.toInstant())

        val redisKey = "refresh:$email"
        cachePort.save(redisKey, refreshToken, duration)

        return SelfSignedJWT(accessToken, refreshToken)
    }

    override fun reissueJwtToken(refreshToken: String): SelfSignedJWT {
        val username = jwtTokenUtil.getUsernameFromToken(refreshToken)
        val role = jwtTokenUtil.getRoleFromToken(refreshToken)

        //a. 토큰 유효성 검사
        if (!jwtTokenUtil.validateToken(refreshToken)) {
            throw ImHereTokenInvalidException()
        }
        //b. 토큰 만료 시간 검사
        if (jwtTokenUtil.getExpirationDateFromToken(refreshToken).isBefore(LocalDateTime.now())) {
            throw ImHereTokenExpiredException()
        }

        val refreshTokenFromRedis = cachePort.find("refresh:$username") as String?

        if (refreshTokenFromRedis != null && refreshTokenFromRedis == refreshToken) {
            return issueJwtAuth(username, role)
        }

        throw IllegalArgumentException("일치하지 않는 리프레시 토큰")
    }
}