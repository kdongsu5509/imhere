package com.kdongsu5509.imhere.auth.application.service.oidc

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.security.KeyFactory
import java.security.spec.RSAPublicKeySpec
import java.time.Instant
import java.util.*

class KakaoOidcSelfSignedJWTTokenSignatureVerifierTest {

    private lateinit var kakaoOidcJwtTokenSignatureVerifier: KakaoOidcJwtTokenSignatureVerifier

    @BeforeEach
    fun setUp() {
        kakaoOidcJwtTokenSignatureVerifier = KakaoOidcJwtTokenSignatureVerifier()
    }

    @Test
    fun `유효한 토큰 서명 검증 성공`() {
        // given
        val idToken = TestJwtBuilder.buildValidIdToken()
        val (modulus, exponent) = getModulusAndExponent()

        // when
        val jws = kakaoOidcJwtTokenSignatureVerifier.verifyTokenSignature(idToken, modulus, exponent)

        // then
        assertNotNull(jws)
        assertNotNull(jws.body)
        assertEquals(TestJwtBuilder.KAKAO_PAYLOAD_ISS, jws.body.issuer)
        assertEquals(TestJwtBuilder.KAKAO_PAYLOAD_AUD, jws.body.audience)
        assertEquals(TestJwtBuilder.KAKAO_PAYLOAD_SUB, jws.body.subject)
    }

    @Test
    fun `만료된 토큰 검증 시 예외 발생`() {
        // given
        val expiredToken = buildExpiredToken()
        val (modulus, exponent) = getModulusAndExponent()

        // when & then
        val exception = assertThrows(SecurityException::class.java) {
            kakaoOidcJwtTokenSignatureVerifier.verifyTokenSignature(expiredToken, modulus, exponent)
        }

        assertTrue(exception.message!!.contains("만료된 토큰"))
    }

    @Test
    fun `잘못된 서명으로 검증 시 예외 발생`() {
        // given
        val idToken = TestJwtBuilder.buildValidIdToken()
        // 다른 키의 modulus와 exponent 사용
        val wrongKeyPair = java.security.KeyPairGenerator.getInstance("RSA").generateKeyPair()
        val keyFactory = KeyFactory.getInstance("RSA")
        val keySpec = keyFactory.getKeySpec(wrongKeyPair.public, RSAPublicKeySpec::class.java)
        val wrongModulus = Base64.getUrlEncoder().withoutPadding()
            .encodeToString(keySpec.modulus.toByteArray())
        val wrongExponent = Base64.getUrlEncoder().withoutPadding()
            .encodeToString(keySpec.publicExponent.toByteArray())

        // when & then
        assertThrows(SecurityException::class.java) {
            kakaoOidcJwtTokenSignatureVerifier.verifyTokenSignature(idToken, wrongModulus, wrongExponent)
        }
    }

    /**
     * 테스트용 키 쌍에서 modulus와 exponent 추출
     */
    private fun getModulusAndExponent(): Pair<String, String> {
        val keyPair = TestJwtBuilder.keyPair
        val publicKey = keyPair.public
        val keyFactory = KeyFactory.getInstance("RSA")
        val keySpec = keyFactory.getKeySpec(publicKey, RSAPublicKeySpec::class.java)

        val modulus = Base64.getUrlEncoder().withoutPadding()
            .encodeToString(keySpec.modulus.toByteArray())
        val exponent = Base64.getUrlEncoder().withoutPadding()
            .encodeToString(keySpec.publicExponent.toByteArray())

        return Pair(modulus, exponent)
    }

    /**
     * 만료된 토큰 생성
     */
    private fun buildExpiredToken(): String {
        val now = Instant.now()
        val issuedAt = Date.from(now.minusSeconds(7200)) // 2시간 전
        val expiration = Date.from(now.minusSeconds(3600)) // 1시간 전 (이미 만료됨)

        val payload: Map<String, Any> = mapOf(
            "iss" to TestJwtBuilder.KAKAO_PAYLOAD_ISS,
            "aud" to TestJwtBuilder.KAKAO_PAYLOAD_AUD,
            "sub" to TestJwtBuilder.KAKAO_PAYLOAD_SUB,
            "iat" to issuedAt,
            "exp" to expiration,
            "email" to TestJwtBuilder.KAKAO_PAYLOAD_EMAIL
        )

        return Jwts.builder()
            .setHeaderParams(
                mapOf(
                    "typ" to TestJwtBuilder.KAKAO_HEADER_TYP,
                    "kid" to TestJwtBuilder.KAKAO_HEADER_KID,
                    "alg" to TestJwtBuilder.KAKAO_HEADER_ALG
                )
            )
            .setClaims(payload)
            .signWith(TestJwtBuilder.testPrivateKey, SignatureAlgorithm.RS256)
            .compact()
    }
}

