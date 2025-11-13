package com.kdongsu5509.imhere.auth.application.port.out

import com.kdongsu5509.imhere.auth.application.dto.OIDCDecodePayload

interface JwtParserPort {
    fun parse(idToken: String): OIDCDecodePayload
}