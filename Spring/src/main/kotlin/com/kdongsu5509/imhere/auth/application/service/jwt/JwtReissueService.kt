package com.kdongsu5509.imhere.auth.application.service.jwt

import com.kdongsu5509.imhere.auth.application.dto.SelfSignedJWT
import com.kdongsu5509.imhere.auth.application.port.`in`.ReissueJWTPort
import org.springframework.stereotype.Component

@Component
class JwtReissueService(private val jwtTokenProvider: JwtTokenProvider) : ReissueJWTPort{
    override fun reissue(refreshToken: String): SelfSignedJWT {
        return jwtTokenProvider.reissueJwtToken(refreshToken)
    }
}