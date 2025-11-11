package com.kdongsu5509.imhere.auth.application.port.out

import com.kdongsu5509.imhere.auth.domain.User

interface SaveUserPort {
    fun save(user: User)
}