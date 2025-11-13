package com.kdongsu5509.imhere.auth.application.port.out

import com.kdongsu5509.imhere.auth.adapter.out.dto.OIDCPublicKey

interface LoadPublicKeyPort {
    fun loadPublicKey(kid: String): OIDCPublicKey
}