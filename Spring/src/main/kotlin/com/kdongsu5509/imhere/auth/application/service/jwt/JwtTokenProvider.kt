package com.kdongsu5509.imhere.auth.application.service.jwt

import com.kdongsu5509.imhere.auth.application.dto.SelfSignedJWT

interface JwtTokenProvider {
    fun issueJwtAuth(email: String, role: String): SelfSignedJWT

    fun reissueJwtToken(refreshToken: String): SelfSignedJWT
}