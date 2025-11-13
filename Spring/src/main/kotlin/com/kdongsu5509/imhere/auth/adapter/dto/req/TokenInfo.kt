package com.kdongsu5509.imhere.auth.adapter.dto.req

import com.kdongsu5509.imhere.auth.domain.OAuth2Provider

data class TokenInfo(val provider: OAuth2Provider, val idToken: String)