package com.kdongsu5509.imhere.auth.application.service.jwt

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "jwt")
data class JwtProperties(
    var secret: String = "",

    var accessExpirationMinutes: Long = 0,

    var refreshExpirationDays: Long = 0,

    var accessHeaderName: String = "",
)