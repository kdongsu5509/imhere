package com.kdongsu5509.imhere.auth.adapter.out.dto

data class OIDCPublicKey(
    val kid: String = "",
    val kty: String = "",
    val alg: String = "",
    val use: String = "",
    val n: String = "", // modulus
    val e: String = ""  // exponent
)