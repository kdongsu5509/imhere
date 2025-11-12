package com.kdongsu5509.imhere.auth.domain

import com.kdongsu5509.imhere.auth.adapter.out.persistence.UserJpaEntity

data class User(
    var email: String,
    var oauthProvider: OAuth2Provider,
    var role: UserRole
)