package com.kdongsu5509.imhere.auth.domain

data class User(
    var email: String,
    var oauthProvider: OAuth2Provider,
    var role: UserRole
)