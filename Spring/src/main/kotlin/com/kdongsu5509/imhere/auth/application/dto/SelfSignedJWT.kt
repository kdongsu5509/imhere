package com.kdongsu5509.imhere.auth.application.dto

import com.kdongsu5509.imhere.auth.adapter.dto.ImhereJwt

data class SelfSignedJWT(
    val accessToken: String,
    val refreshToken: String
) {
    fun toImhereJwt(): ImhereJwt =
        ImhereJwt(this.accessToken, this.refreshToken)
}
