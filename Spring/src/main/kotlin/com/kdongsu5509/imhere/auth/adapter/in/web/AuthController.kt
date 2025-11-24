package com.kdongsu5509.imhere.auth.adapter.`in`.web

import JwtRefreshToken
import com.kdongsu5509.imhere.auth.adapter.dto.req.TokenInfo
import com.kdongsu5509.imhere.auth.adapter.dto.resp.ImhereJwt
import com.kdongsu5509.imhere.auth.application.dto.SelfSignedJWT
import com.kdongsu5509.imhere.auth.application.port.`in`.HandleOIDCUseCase
import com.kdongsu5509.imhere.auth.application.port.`in`.ReissueJWTPort
import lombok.extern.slf4j.Slf4j
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
class AuthController(val handleOIDCUseCase: HandleOIDCUseCase, val reissueJwtPort: ReissueJWTPort) {

    @PostMapping("/login")
    fun handleIdToken(
        @Validated @RequestBody tokenInfo: TokenInfo
    ): ImhereJwt {
        val jwt: SelfSignedJWT = handleOIDCUseCase.verifyIdTokenAndReturnJwt(
            tokenInfo.idToken, tokenInfo.provider
        )

        return ImhereJwt(jwt.accessToken, jwt.refreshToken)
    }

    @PostMapping("/reissue")
    fun handleJwtTokenReissueRequest(
        @Validated @RequestBody jwtRefreshToken: JwtRefreshToken
    ): ImhereJwt {
        val jwt = reissueJwtPort.reissue(jwtRefreshToken.refreshToken)
        return ImhereJwt(jwt.accessToken, jwt.refreshToken)
    }
}