package com.kdongsu5509.imhere.auth.adapter.dto

import com.kdongsu5509.imhere.auth.application.dto.SelfSignedJWT

data class ImhereJwt(
    val accssToken: String,
    val refreshToken: String
) {
    fun selfSignedJWTToImhereJwt(selfSignedJWT: SelfSignedJWT): ImhereJwt {
        return ImhereJwt(
            selfSignedJWT.accessToken,
            selfSignedJWT.refreshToken
        )
    }
}
