package com.kdongsu5509.imhere.auth.application.port.`in`

import com.kdongsu5509.imhere.auth.application.dto.SelfSignedJWT
import com.kdongsu5509.imhere.auth.domain.OAuth2Provider

interface handleOIDCUseCase {
    fun verifyIdTokenAndReturnJwt(idToken: String, provider: OAuth2Provider): SelfSignedJWT
}