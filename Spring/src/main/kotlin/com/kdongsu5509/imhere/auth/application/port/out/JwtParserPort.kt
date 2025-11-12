package com.kdongsu5509.imhere.auth.application.port.out

interface JwtParserPort {
    fun parse(idToken: String)
}