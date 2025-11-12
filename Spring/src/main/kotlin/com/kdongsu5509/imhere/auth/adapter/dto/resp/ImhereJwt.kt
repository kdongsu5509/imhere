package com.kdongsu5509.imhere.auth.adapter.dto.resp

data class ImhereJwt(
    val accessToken: String,
    val refreshToken: String
)