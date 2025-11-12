package com.kdongsu5509.imhere.auth.application.port.`in`

import com.kdongsu5509.imhere.auth.application.dto.SelfSignedJWT
import com.kdongsu5509.imhere.auth.domain.OAuth2Provider

interface IssueJWTPort {
    fun issue(oidc: String, oauth2Provider: OAuth2Provider): SelfSignedJWT
}