package com.kdongsu5509.imhere.auth.adapter.out.jjwt

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "oidc.kakao")
data class KakaoOIDCProperties(
    var issuer: String = "",
    var audience: String = "",
    var cacheKey: String = ""
)