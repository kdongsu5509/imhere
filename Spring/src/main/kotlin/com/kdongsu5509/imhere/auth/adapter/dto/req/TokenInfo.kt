package com.kdongsu5509.imhere.auth.adapter.dto.req

import com.kdongsu5509.imhere.auth.domain.OAuth2Provider
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty

data class TokenInfo(
    @NotEmpty(message = "OAuth2 제공자는 필수입니다")
    val provider: OAuth2Provider,
    @NotBlank(message = "idToken은 필수입니다.")
    val idToken: String,
)