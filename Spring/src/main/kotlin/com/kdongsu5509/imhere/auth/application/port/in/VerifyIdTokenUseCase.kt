package com.kdongsu5509.imhere.auth.application.port.`in`

interface VerifyIdTokenUseCase {
    fun verifyIdTokenAndReturnUserEmail(idToken: String): String
}