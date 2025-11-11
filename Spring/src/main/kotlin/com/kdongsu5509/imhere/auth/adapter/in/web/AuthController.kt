package com.kdongsu5509.imhere.auth.adapter.`in`.web

import com.kdongsu5509.imhere.auth.adapter.dto.ImhereJwt
import com.kdongsu5509.imhere.auth.adapter.dto.TokenInfo
import com.kdongsu5509.imhere.auth.application.port.`in`.handleOIDCUseCase
import lombok.extern.slf4j.Slf4j
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
class AuthController(val verifyIdTokenUserCase: handleOIDCUseCase) {

    @PostMapping
    fun handleIdToken(@RequestBody tokenInfo: TokenInfo): ImhereJwt {
        return verifyIdTokenUserCase.verifyIdTokenAndReturnJwt(tokenInfo.idToken, tokenInfo.provider)
            .toImhereJwt()
    }
}