package com.kdongsu5509.imhere.auth.application.service.oidc.kakao

import com.kdongsu5509.imhere.auth.adapter.out.kakao.KakaoOIDCProperties
import com.kdongsu5509.imhere.auth.application.dto.UserInformation
import com.kdongsu5509.imhere.auth.application.port.out.JwtParserPort
import com.kdongsu5509.imhere.auth.application.service.oidc.KakaoOidcIdTokenPayloadVerifier
import com.kdongsu5509.imhere.auth.application.service.oidc.KakaoOidcTokenVerificationHelper
import com.kdongsu5509.imhere.auth.application.port.out.OIDCVerificationPort
import io.jsonwebtoken.MalformedJwtException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class KakaoOIDCVerificationService(
    private val JwtParserPort: JwtParserPort,
    private val kakaoOIDCProperties: KakaoOIDCProperties,
    private val kakaoOidcTokenVerificationHelper: KakaoOidcTokenVerificationHelper,
    private val kakaoOidcIdTokenPayloadVerifier: KakaoOidcIdTokenPayloadVerifier
): OIDCVerificationPort {

    override fun verifyAndReturnUserInformation(idToken: String): UserInformation {
        val payload = JwtParserPort.parse(idToken)
        try {
            val payload = kakaoOidcTokenVerificationHelper.getPayloadFromIdToken(
                idToken,
                kakaoOIDCProperties.issuer,
                kakaoOIDCProperties.audience,
                cachedKeySet
            )

            kakaoOidcIdTokenPayloadVerifier.verifyPayload(
                payload,
                kakaoOIDCProperties.audience  // expected audience
            )

            //4. 사용자 정보 확인
            payload.email ?: throw MalformedJwtException("ID 토큰에 이메일 정보가 없습니다.")

            return UserInformation(payload.email)
        } catch (e: SecurityException) {
            throw e
        } catch (e: Exception) {
            throw SecurityException("ID 토큰 검증에 실패했습니다. (${e.message})", e)
        }
    }
}