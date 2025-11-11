package com.kdongsu5509.imhere.auth.adapter.`in`.web

import com.kdongsu5509.imhere.auth.adapter.dto.TokenInfo
import com.kdongsu5509.imhere.auth.application.port.`in`.VerifyIdTokenUseCase
import lombok.extern.slf4j.Slf4j
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
class AuthController(val verifyIdTokenUserCase: VerifyIdTokenUseCase) {

    @PostMapping
    fun handleIdToken(@RequestBody tokenInfo: TokenInfo): String? {
        val userEmail = verifyIdTokenUserCase.verifyIdTokenAndReturnUserEmail(tokenInfo.idToken) // 토큰 검증 위임.

        //회원 정보 조회
        // 만약 신규 -> 회원가입 로직 동작
        // 아니라면 -> JWT 발급 로직 동작
        // JWT 를 발급
        return "verifyIdToken"
    }
}