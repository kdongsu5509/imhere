package com.kdongsu5509.imhere.auth.application.service.oidc

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.security.KeyFactory
import java.security.spec.RSAPublicKeySpec
import java.util.*

class KakaoOidcSelfSignedJWTTokenParserTest {

    private lateinit var kakaoOidcJwtTokenParser: KakaoOidcJwtTokenParser

    @BeforeEach
    fun setUp() {
        kakaoOidcJwtTokenParser = KakaoOidcJwtTokenParser()
    }

    @Test
    fun `유효한 토큰에서 kid 추출 성공`() {
        // given
        val idToken = TestJwtBuilder.buildValidIdToken()
        val iss = TestJwtBuilder.KAKAO_PAYLOAD_ISS
        val aud = TestJwtBuilder.KAKAO_PAYLOAD_AUD

        // when
        val kid = kakaoOidcJwtTokenParser.getKidFromOriginTokenHeader(idToken, iss, aud)

        // then
        assertEquals(TestJwtBuilder.KAKAO_HEADER_KID, kid)
    }

    @Test
    fun `잘못된 issuer로 kid 추출 시 예외 발생`() {
        // given
        val idToken = TestJwtBuilder.buildValidIdToken()
        val invalidIss = "https://invalid.issuer.com"
        val aud = TestJwtBuilder.KAKAO_PAYLOAD_AUD

        // when & then
        assertThrows(SecurityException::class.java) {
            kakaoOidcJwtTokenParser.getKidFromOriginTokenHeader(idToken, invalidIss, aud)
        }
    }

    @Test
    fun `잘못된 audience로 kid 추출 시 예외 발생`() {
        // given
        val idToken = TestJwtBuilder.buildValidIdToken()
        val iss = TestJwtBuilder.KAKAO_PAYLOAD_ISS
        val invalidAud = "invalid-audience"

        // when & then
        assertThrows(SecurityException::class.java) {
            kakaoOidcJwtTokenParser.getKidFromOriginTokenHeader(idToken, iss, invalidAud)
        }
    }

    @Test
    fun `잘못된 형식의 토큰 파싱 시 예외 발생`() {
        // given
        val invalidToken = "invalid.token.format" // 서명 부분이 없음

        // when & then
        assertThrows(SecurityException::class.java) {
            kakaoOidcJwtTokenParser.getKidFromOriginTokenHeader(
                invalidToken,
                TestJwtBuilder.KAKAO_PAYLOAD_ISS,
                TestJwtBuilder.KAKAO_PAYLOAD_AUD
            )
        }
    }

    @Test
    fun `검증된 JWS 객체에서 페이로드 추출 성공`() {
        // given
        val idToken = TestJwtBuilder.buildValidIdToken()
        val (modulus, exponent) = getModulusAndExponent()

        // 검증기를 통해 JWS 객체 생성
        val verifier = KakaoOidcJwtTokenSignatureVerifier()
        val jws = verifier.verifyTokenSignature(idToken, modulus, exponent)

        // when
        val payload = kakaoOidcJwtTokenParser.extractPayloadFromJws(jws)

        // then
        assertEquals(TestJwtBuilder.KAKAO_PAYLOAD_ISS, payload.iss)
        assertEquals(TestJwtBuilder.KAKAO_PAYLOAD_AUD, payload.aud)
        assertEquals(TestJwtBuilder.KAKAO_PAYLOAD_SUB, payload.sub)
        assertEquals(TestJwtBuilder.KAKAO_PAYLOAD_EMAIL, payload.email)
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
}

