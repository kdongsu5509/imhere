package com.kdongsu5509.imhere.auth.application.port.out

interface CheckUserPort {
    fun existsByEmail(email: String): Boolean
}