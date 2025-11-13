package com.kdongsu5509.imhere.auth.adapter.out.jjwt

import com.kdongsu5509.imhere.auth.application.dto.OIDCDecodePayload
import com.kdongsu5509.imhere.auth.application.service.oidc.TestJwtBuilder
import com.kdongsu5509.imhere.common.exception.implementation.auth.InvalidEncodingException
import com.kdongsu5509.imhere.common.exception.implementation.auth.OIDCExpiredException
import com.kdongsu5509.imhere.common.exception.implementation.auth.OIDCInvalidException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.security.KeyPairGenerator
import java.time.Instant
import java.util.*

class JjwtVerifyAdapterTest {

    private lateinit var kakaoOIDCProperties: KakaoOIDCProperties
    private lateinit var jjwtVerifyAdapter: JjwtVerifyAdapter

    @BeforeEach
    fun setUp() {
        kakaoOIDCProperties = KakaoOIDCProperties(
            issuer = TestJwtBuilder.KAKAO_PAYLOAD_ISS,
            audience = TestJwtBuilder.KAKAO_PAYLOAD_AUD,
            cacheKey = "test-cache-key"
        )
        jjwtVerifyAdapter = JjwtVerifyAdapter(kakaoOIDCProperties)
    }

    @Test
    @DisplayName("유효한 payload를 검증하면 성공한다")
    fun verifyPayLoad_validPayload_success() {
        // given
        val payload = OIDCDecodePayload(
            iss = TestJwtBuilder.KAKAO_PAYLOAD_ISS,
            aud = TestJwtBuilder.KAKAO_PAYLOAD_AUD,
            sub = TestJwtBuilder.KAKAO_PAYLOAD_SUB,
            email = TestJwtBuilder.KAKAO_PAYLOAD_EMAIL
        )

        // when & then
        jjwtVerifyAdapter.verifyPayLoad(payload)
        // 예외가 발생하지 않으면 성공
    }

    @Test
    @DisplayName("issuer가 일치하지 않으면 OIDCInvalidException을 던진다")
    fun verifyPayLoad_invalidIssuer_throwsException() {
        // given
        val payload = OIDCDecodePayload(
            iss = "https://invalid-issuer.com",
            aud = TestJwtBuilder.KAKAO_PAYLOAD_AUD,
            sub = TestJwtBuilder.KAKAO_PAYLOAD_SUB,
            email = TestJwtBuilder.KAKAO_PAYLOAD_EMAIL
        )

        // when & then
        assertThrows<OIDCInvalidException> {
            jjwtVerifyAdapter.verifyPayLoad(payload)
        }.also { exception ->
            assertThat(exception.message).contains("issuer가 일치하지 않습니다")
            assertThat(exception.message).contains("https://invalid-issuer.com")
        }
    }

    @Test
    @DisplayName("audience가 일치하지 않으면 OIDCInvalidException을 던진다")
    fun verifyPayLoad_invalidAudience_throwsException() {
        // given
        val payload = OIDCDecodePayload(
            iss = TestJwtBuilder.KAKAO_PAYLOAD_ISS,
            aud = "invalid-audience",
            sub = TestJwtBuilder.KAKAO_PAYLOAD_SUB,
            email = TestJwtBuilder.KAKAO_PAYLOAD_EMAIL
        )

        // when & then
        assertThrows<OIDCInvalidException> {
            jjwtVerifyAdapter.verifyPayLoad(payload)
        }.also { exception ->
            assertThat(exception.message).contains("audience가 일치하지 않습니다")
            assertThat(exception.message).contains("invalid-audience")
        }
    }

    @Test
    @DisplayName("유효한 서명으로 토큰을 검증하면 성공한다")
    fun verifySignature_validToken_success() {
        // given
        val validToken = TestJwtBuilder.buildValidIdToken()
        val keyPair = TestJwtBuilder.keyPair
        val rsaPublicKey = keyPair.public as java.security.interfaces.RSAPublicKey

        // RSA 공개키를 modulus와 exponent로 변환 (Base64 URL 인코딩)
        // BigInteger.toByteArray()는 부호 있는 바이트 배열을 반환하므로, 양수로 변환
        val nBytes = rsaPublicKey.modulus.toByteArray()
        val eBytes = rsaPublicKey.publicExponent.toByteArray()

        // 앞의 0 바이트 제거 후 Base64 URL 인코딩 (부호 바이트 제거)
        val nEncoded = Base64.getUrlEncoder().withoutPadding().encodeToString(
            if (nBytes.isNotEmpty() && nBytes[0] == 0.toByte()) nBytes.sliceArray(1 until nBytes.size) else nBytes
        )
        val eEncoded = Base64.getUrlEncoder().withoutPadding().encodeToString(
            if (eBytes.isNotEmpty() && eBytes[0] == 0.toByte()) eBytes.sliceArray(1 until eBytes.size) else eBytes
        )

        // when
        val result = jjwtVerifyAdapter.verifySignature(validToken, nEncoded, eEncoded)

        // then
        assertThat(result).isNotNull()
        assertThat(result.body.issuer).isEqualTo(TestJwtBuilder.KAKAO_PAYLOAD_ISS)
        assertThat(result.body.audience).isEqualTo(TestJwtBuilder.KAKAO_PAYLOAD_AUD)
    }

    @Test
    @DisplayName("만료된 토큰을 검증하면 OIDCExpiredException을 던진다")
    fun verifySignature_expiredToken_throwsException() {
        // given
        val keyPair = KeyPairGenerator.getInstance("RSA").generateKeyPair()
        val expiredToken = Jwts.builder()
            .setHeaderParam("kid", "test-kid")
            .setIssuer(TestJwtBuilder.KAKAO_PAYLOAD_ISS)
            .setAudience(TestJwtBuilder.KAKAO_PAYLOAD_AUD)
            .setSubject(TestJwtBuilder.KAKAO_PAYLOAD_SUB)
            .setIssuedAt(Date.from(Instant.now().minusSeconds(7200)))
            .setExpiration(Date.from(Instant.now().minusSeconds(3600))) // 이미 만료됨
            .signWith(keyPair.private, SignatureAlgorithm.RS256)
            .compact()

        val rsaPublicKey = keyPair.public as java.security.interfaces.RSAPublicKey
        val nBytes = rsaPublicKey.modulus.toByteArray()
        val eBytes = rsaPublicKey.publicExponent.toByteArray()
        val nEncoded = Base64.getUrlEncoder().withoutPadding().encodeToString(
            if (nBytes.isNotEmpty() && nBytes[0] == 0.toByte()) nBytes.sliceArray(1 until nBytes.size) else nBytes
        )
        val eEncoded = Base64.getUrlEncoder().withoutPadding().encodeToString(
            if (eBytes.isNotEmpty() && eBytes[0] == 0.toByte()) eBytes.sliceArray(1 until eBytes.size) else eBytes
        )

        // when & then
        assertThrows<OIDCExpiredException> {
            jjwtVerifyAdapter.verifySignature(expiredToken, nEncoded, eEncoded)
        }
    }

    @Test
    @DisplayName("잘못된 modulus 형식으로 공개키 생성 시 InvalidEncodingException을 던진다")
    fun verifySignature_invalidModulusEncoding_throwsException() {
        // given
        val validToken = TestJwtBuilder.buildValidIdToken()
        val invalidModulus = "invalid-base64-encoding!!!"
        val validExponent = Base64.getUrlEncoder().withoutPadding().encodeToString(
            byteArrayOf(1, 0, 1)
        )

        // when & then
        assertThrows<InvalidEncodingException> {
            jjwtVerifyAdapter.verifySignature(validToken, invalidModulus, validExponent)
        }
    }

    @Test
    @DisplayName("잘못된 exponent 형식으로 공개키 생성 시 InvalidEncodingException을 던진다")
    fun verifySignature_invalidExponentEncoding_throwsException() {
        // given
        val validToken = TestJwtBuilder.buildValidIdToken()
        val keyPair = TestJwtBuilder.keyPair
        val rsaPublicKey = keyPair.public as java.security.interfaces.RSAPublicKey
        val nBytes = rsaPublicKey.modulus.toByteArray()
        val validModulus = Base64.getUrlEncoder().withoutPadding().encodeToString(
            if (nBytes[0] == 0.toByte()) nBytes.sliceArray(1 until nBytes.size) else nBytes
        )
        val invalidExponent = "invalid-base64-encoding!!!"

        // when & then
        assertThrows<InvalidEncodingException> {
            jjwtVerifyAdapter.verifySignature(validToken, validModulus, invalidExponent)
        }
    }

    @Test
    @DisplayName("잘못된 서명으로 토큰을 검증하면 예외를 던진다")
    fun verifySignature_invalidSignature_throwsException() {
        // given
        val validToken = TestJwtBuilder.buildValidIdToken()
        // 다른 키 페어로 공개키 생성
        val differentKeyPair = KeyPairGenerator.getInstance("RSA").generateKeyPair()
        val rsaPublicKey = differentKeyPair.public as java.security.interfaces.RSAPublicKey
        val nBytes = rsaPublicKey.modulus.toByteArray()
        val eBytes = rsaPublicKey.publicExponent.toByteArray()
        val nEncoded = Base64.getUrlEncoder().withoutPadding().encodeToString(
            if (nBytes[0] == 0.toByte()) nBytes.sliceArray(1 until nBytes.size) else nBytes
        )
        val eEncoded = Base64.getUrlEncoder().withoutPadding().encodeToString(
            if (eBytes[0] == 0.toByte()) eBytes.sliceArray(1 until eBytes.size) else eBytes
        )

        // when & then
        assertThrows<Exception> {
            jjwtVerifyAdapter.verifySignature(validToken, nEncoded, eEncoded)
        }
    }
}
