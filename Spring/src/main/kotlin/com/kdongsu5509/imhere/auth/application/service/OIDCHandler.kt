package com.kdongsu5509.imhere.auth.application.service

import com.kdongsu5509.imhere.auth.application.dto.SelfSignedJWT
import com.kdongsu5509.imhere.auth.application.port.`in`.handleOIDCUseCase
import com.kdongsu5509.imhere.auth.domain.OAuth2Provider

abstract class OIDCHandler : handleOIDCUseCase {
    abstract fun verifyOIDC(oidc: String): String
    abstract fun issueJwt(email: String, provider: OAuth2Provider): SelfSignedJWT
}