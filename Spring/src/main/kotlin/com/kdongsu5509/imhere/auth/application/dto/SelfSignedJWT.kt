package com.kdongsu5509.imhere.auth.application.dto

data class SelfSignedJWT(
    val accessToken: String,
    val refreshToken: String
)