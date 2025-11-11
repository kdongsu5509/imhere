package com.kdongsu5509.imhere.auth.adapter.out.dto

data class OIDCPublicKeyResponse(
    val keys: List<OIDCPublicKey> = emptyList()
)