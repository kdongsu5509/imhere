package com.kdongsu5509.imhere

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.time.Instant
import java.util.*

object TestJwtBuilder {

    const val KAKAO_HEADER_ALG = "RS256"
    const val KAKAO_HEADER_TYP = "JWT"
    const val KAKAO_HEADER_KID = "test-kid"

    const val KAKAO_PAYLOAD_ISS = "https://kauth.kakao.com"
    const val KAKAO_PAYLOAD_AUD = "bf284f33bfeba9bc59575706d0eb0e9c" // 테스트용 앱 키
    const val KAKAO_PAYLOAD_SUB = "사용자회원번호"
    const val KAKAO_PAYLOAD_EMAIL = "ds.ko@kakao.com"
    const val KAKAO_PAYLOAD_EXP_SECONDS = 3600L

    val keyPair: KeyPair = KeyPairGenerator.getInstance("RSA").generateKeyPair()
    val testPublicKey = keyPair.public
    val testPrivateKey = keyPair.private


    /**
     * 유효한 카카오 규격의 ID 토큰을 생성하는 빌더 메서드입니다.
     */
    fun buildValidIdToken(): String {

        val now = Instant.now()
        val issuedAt = Date.from(now)
        val expiration = Date.from(now.plusSeconds(KAKAO_PAYLOAD_EXP_SECONDS))

        // **1. 모든 클레임을 Map으로 정의**
        val payload: Map<String, Any> = mapOf(
            // 표준 클레임
            "iss" to KAKAO_PAYLOAD_ISS,
            "aud" to KAKAO_PAYLOAD_AUD,
            "sub" to KAKAO_PAYLOAD_SUB,
            "iat" to issuedAt,
            "exp" to expiration,

            // 카카오 OIDC/추가 클레임
            "auth_time" to issuedAt, // iat와 동일하게 설정
            "nonce" to UUID.randomUUID().toString(),
            "email" to KAKAO_PAYLOAD_EMAIL
        )

        val testJwt = Jwts.builder()

            // **2. 헤더 설정 (setHeaderParams 사용)**
            .setHeaderParams(
                mapOf(
                    "typ" to KAKAO_HEADER_TYP,
                    "kid" to KAKAO_HEADER_KID,
                    "alg" to KAKAO_HEADER_ALG
                )
            )

            // **3. Map으로 정의된 클레임을 한 번에 전달**
            .setClaims(payload)

            // 4. 서명
            .signWith(testPrivateKey, SignatureAlgorithm.RS256)
            .compact()

        print("TEST JWT : $testJwt")
        return testJwt
    }
}