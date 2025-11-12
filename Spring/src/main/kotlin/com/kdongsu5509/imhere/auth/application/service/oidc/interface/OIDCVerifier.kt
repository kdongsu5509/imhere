package com.kdongsu5509.imhere.auth.application.service.oidc.`interface`

import com.kdongsu5509.imhere.auth.application.dto.UserInformation

interface OIDCVerifier {
    fun verifyAndReturnUserInformation(idToken: String): UserInformation
}