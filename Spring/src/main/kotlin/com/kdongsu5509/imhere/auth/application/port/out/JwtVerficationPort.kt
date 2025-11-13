package com.kdongsu5509.imhere.auth.application.port.out

import com.kdongsu5509.imhere.auth.application.dto.OIDCDecodePayload
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jws

interface JwtVerficationPort {
    fun verifyPayLoad(payload: OIDCDecodePayload)

    fun verifySignature(
        token: String,
        modulus: String,
        exponent: String
    ): Jws<Claims>
}