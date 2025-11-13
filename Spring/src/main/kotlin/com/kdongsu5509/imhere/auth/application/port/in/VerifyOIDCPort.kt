package com.kdongsu5509.imhere.auth.application.port.`in`

import com.kdongsu5509.imhere.auth.application.dto.UserInformation
import com.kdongsu5509.imhere.auth.domain.OAuth2Provider

interface VerifyOIDCPort {
    fun verify(oidc: String, oAuth2Provider: OAuth2Provider): UserInformation
}