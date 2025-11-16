package com.kdongsu5509.imhere.auth.application.port.`in`

import com.kdongsu5509.imhere.auth.application.dto.SelfSignedJWT

interface ReissueJWTPort {
    fun reissue(refreshToken: String): SelfSignedJWT
}