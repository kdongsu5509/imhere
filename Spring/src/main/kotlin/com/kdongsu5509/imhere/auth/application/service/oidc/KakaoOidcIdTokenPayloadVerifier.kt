package com.kdongsu5509.imhere.auth.application.service.oidc

import com.kdongsu5509.imhere.auth.application.dto.OIDCDecodePayload
import org.springframework.stereotype.Component

@Component
class KakaoOidcIdTokenPayloadVerifier {

    companion object {
        private const val KAKAO_ISSUER = "https://kauth.kakao.com"
    }

    fun verifyPayload(
        payload: OIDCDecodePayload,
        expectedAudience: String,
    ) {
        verifyIssuer(payload.iss)
        verifyAudience(payload.aud, expectedAudience)
    }

    private fun verifyIssuer(actualIssuer: String) {
        if (actualIssuer != KAKAO_ISSUER) {
            throw SecurityException(
                "토큰의 issuer가 일치하지 않습니다. 예상: $KAKAO_ISSUER, 실제: $actualIssuer"
            )
        }
    }

    private fun verifyAudience(actualAudience: String, expectedAudience: String) {
        if (actualAudience != expectedAudience) {
            throw SecurityException(
                "토큰의 audience가 일치하지 않습니다. 예상: $expectedAudience, 실제: $actualAudience"
            )
        }
    }
}

