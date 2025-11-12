package com.kdongsu5509.imhere.auth.application.port.out

import com.kdongsu5509.imhere.auth.application.dto.UserInformation

interface OIDCVerificationPort {
    fun verifyAndReturnUserInformation(idToken: String): UserInformation
}