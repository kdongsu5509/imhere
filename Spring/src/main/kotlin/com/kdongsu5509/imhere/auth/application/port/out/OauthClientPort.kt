package com.kdongsu5509.imhere.auth.application.port.out

import com.kdongsu5509.imhere.auth.adapter.out.dto.OIDCPublicKeyResponse

interface OauthClientPort {
    fun getPublicKeyFromProvider(): OIDCPublicKeyResponse?
}