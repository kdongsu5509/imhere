package com.kdongsu5509.imhere.auth.application.service.oidc.kakao

import com.kdongsu5509.imhere.auth.application.dto.UserInformation
import com.kdongsu5509.imhere.auth.application.port.out.JwtParserPort
import com.kdongsu5509.imhere.auth.application.port.out.JwtVerficationPort
import com.kdongsu5509.imhere.auth.application.port.out.OIDCVerificationPort
import io.jsonwebtoken.MalformedJwtException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class KakaoOIDCVerificationService(
    private val JwtParserPort: JwtParserPort,
    private val jwtVerficationPort: JwtVerficationPort
) : OIDCVerificationPort {

    override fun verifyAndReturnUserInformation(idToken: String): UserInformation {
        val payload = JwtParserPort.parse(idToken)
        jwtVerficationPort.verifyPayLoad(payload)
        payload.email ?: throw MalformedJwtException("ID 토큰에 이메일 정보가 없습니다")
        return UserInformation(payload.email)
    }
}