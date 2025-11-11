package com.kdongsu5509.imhere.auth.application.service

import com.kdongsu5509.imhere.auth.adapter.out.dto.OIDCPublicKey
import com.kdongsu5509.imhere.auth.adapter.out.dto.OIDCPublicKeyResponse
import com.kdongsu5509.imhere.auth.application.dto.OIDCDecodePayload
import com.kdongsu5509.imhere.auth.application.port.out.CachePort
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.security.KeyFactory
import java.security.spec.RSAPublicKeySpec
import java.util.*

class KakaoOidcIdTokenVerifierTest {

    private lateinit var cachePort: CachePort
    private lateinit var kakaoOidcTokenVerificationHelper: KakaoOidcTokenVerificationHelper
    private lateinit var kakaoOidcIdTokenPayloadVerifier: KakaoOidcIdTokenPayloadVerifier
    private lateinit var kakaoOidcIdTokenVerifier: KakaoOidcIdTokenVerifier

    @BeforeEach
    fun setUp() {
        cachePort = mockk()
        kakaoOidcTokenVerificationHelper = mockk()
        kakaoOidcIdTokenPayloadVerifier = mockk()
        kakaoOidcIdTokenVerifier = KakaoOidcIdTokenVerifier(
            cachePort,
            kakaoOidcTokenVerificationHelper,
            kakaoOidcIdTokenPayloadVerifier
        )
    }

    @Test
    fun `유효한 ID 토큰 검증 성공`() {
        // given
        val idToken = TestJwtBuilder.buildValidIdToken()
        val publicKeyResponse = createMockPublicKeyResponse()
        val expectedPayload = OIDCDecodePayload(
            iss = "https://kauth.kakao.com",
            aud = "bf284f33bfeba9bc59575706d0eb0e9c",
            sub = "사용자회원번호",
            email = "ds.ko@kakao.com"
        )

        every { cachePort.find("KakaoPublicKey::kakaoPublicKeySet") } returns publicKeyResponse
        every {
            kakaoOidcTokenVerificationHelper.getPayloadFromIdToken(
                idToken,
                "https://kauth.kakao.com",
                "bf284f33bfeba9bc59575706d0eb0e9c",
                publicKeyResponse
            )
        } returns expectedPayload
        every {
            kakaoOidcIdTokenPayloadVerifier.verifyPayload(
                expectedPayload,
                "bf284f33bfeba9bc59575706d0eb0e9c",
            )
        } returns Unit

        // when & then (예외가 발생하지 않으면 성공)
        kakaoOidcIdTokenVerifier.verifyIdTokenAndReturnUserEmail(idToken)
        verify(exactly = 1) { cachePort.find("KakaoPublicKey::kakaoPublicKeySet") }
        verify(exactly = 1) {
            kakaoOidcTokenVerificationHelper.getPayloadFromIdToken(
                idToken,
                "https://kauth.kakao.com",
                "bf284f33bfeba9bc59575706d0eb0e9c",
                publicKeyResponse
            )
        }
        verify(exactly = 1) {
            kakaoOidcIdTokenPayloadVerifier.verifyPayload(
                expectedPayload,
                "bf284f33bfeba9bc59575706d0eb0e9c",
            )
        }
    }

    @Test
    fun `공개키 캐시가 비어있을 때 예외 발생`() {
        // given
        val idToken = TestJwtBuilder.buildValidIdToken()

        every { cachePort.find("KakaoPublicKey::kakaoPublicKeySet") } returns null

        // when & then
        val exception = assertThrows(SecurityException::class.java) {
            kakaoOidcIdTokenVerifier.verifyIdTokenAndReturnUserEmail(idToken)
        }

        assertEquals("공개키 캐시가 비어있습니다. 카카오 서버에 요청하여 초기화가 필요합니다.", exception.message)
        verify(exactly = 1) { cachePort.find("KakaoPublicKey::kakaoPublicKeySet") }
        verify(exactly = 0) {
            kakaoOidcTokenVerificationHelper.getPayloadFromIdToken(any(), any(), any(), any())
        }
    }

    @Test
    fun `토큰의 issuer가 일치하지 않을 때 예외 발생`() {
        // given
        val idToken = TestJwtBuilder.buildValidIdToken()
        val publicKeyResponse = createMockPublicKeyResponse()
        val invalidPayload = OIDCDecodePayload(
            iss = "https://invalid.issuer.com", // 잘못된 issuer
            aud = "bf284f33bfeba9bc59575706d0eb0e9c",
            sub = "사용자회원번호",
            email = "ds.ko@kakao.com"
        )

        every { cachePort.find("KakaoPublicKey::kakaoPublicKeySet") } returns publicKeyResponse
        every {
            kakaoOidcTokenVerificationHelper.getPayloadFromIdToken(
                idToken,
                "https://kauth.kakao.com",
                "bf284f33bfeba9bc59575706d0eb0e9c",
                publicKeyResponse
            )
        } returns invalidPayload
        every {
            kakaoOidcIdTokenPayloadVerifier.verifyPayload(
                invalidPayload,
                "bf284f33bfeba9bc59575706d0eb0e9c"
            )
        } throws SecurityException("토큰의 issuer가 일치하지 않습니다. 예상: https://kauth.kakao.com, 실제: https://invalid.issuer.com")

        // when & then
        val exception = assertThrows(SecurityException::class.java) {
            kakaoOidcIdTokenVerifier.verifyIdTokenAndReturnUserEmail(idToken)
        }

        assertTrue(exception.message!!.contains("토큰의 issuer가 일치하지 않습니다"))
        verify(exactly = 1) { cachePort.find("KakaoPublicKey::kakaoPublicKeySet") }
        verify(exactly = 1) {
            kakaoOidcTokenVerificationHelper.getPayloadFromIdToken(any(), any(), any(), any())
        }
        verify(exactly = 1) {
            kakaoOidcIdTokenPayloadVerifier.verifyPayload(any(), any())
        }
    }

    @Test
    fun `토큰의 audience가 일치하지 않을 때 예외 발생`() {
        // given
        val idToken = TestJwtBuilder.buildValidIdToken()
        val publicKeyResponse = createMockPublicKeyResponse()
        val invalidPayload = OIDCDecodePayload(
            iss = "https://kauth.kakao.com",
            aud = "invalid-audience", // 잘못된 audience
            sub = "사용자회원번호",
            email = "ds.ko@kakao.com"
        )

        every { cachePort.find("KakaoPublicKey::kakaoPublicKeySet") } returns publicKeyResponse
        every {
            kakaoOidcTokenVerificationHelper.getPayloadFromIdToken(
                idToken,
                "https://kauth.kakao.com",
                "bf284f33bfeba9bc59575706d0eb0e9c",
                publicKeyResponse
            )
        } returns invalidPayload
        every {
            kakaoOidcIdTokenPayloadVerifier.verifyPayload(
                invalidPayload,
                "bf284f33bfeba9bc59575706d0eb0e9c",
            )
        } throws SecurityException("토큰의 audience가 일치하지 않습니다. 예상: bf284f33bfeba9bc59575706d0eb0e9c, 실제: invalid-audience")

        // when & then
        val exception = assertThrows(SecurityException::class.java) {
            kakaoOidcIdTokenVerifier.verifyIdTokenAndReturnUserEmail(idToken)
        }

        assertTrue(exception.message!!.contains("토큰의 audience가 일치하지 않습니다"))
        verify(exactly = 1) { cachePort.find("KakaoPublicKey::kakaoPublicKeySet") }
        verify(exactly = 1) {
            kakaoOidcTokenVerificationHelper.getPayloadFromIdToken(any(), any(), any(), any())
        }
        verify(exactly = 1) {
            kakaoOidcIdTokenPayloadVerifier.verifyPayload(any(), any())
        }
    }

    @Test
    fun `KakaoOidcTokenVerificationHelper에서 예외 발생 시 SecurityException으로 변환`() {
        // given
        val idToken = TestJwtBuilder.buildValidIdToken()
        val publicKeyResponse = createMockPublicKeyResponse()

        every { cachePort.find("KakaoPublicKey::kakaoPublicKeySet") } returns publicKeyResponse
        every {
            kakaoOidcTokenVerificationHelper.getPayloadFromIdToken(
                idToken,
                "https://kauth.kakao.com",
                "bf284f33bfeba9bc59575706d0eb0e9c",
                publicKeyResponse
            )
        } throws RuntimeException("토큰 검증 실패")

        // when & then
        val exception = assertThrows(SecurityException::class.java) {
            kakaoOidcIdTokenVerifier.verifyIdTokenAndReturnUserEmail(idToken)
        }

        assertTrue(exception.message!!.contains("ID 토큰 검증에 실패했습니다"))
        verify(exactly = 1) { cachePort.find("KakaoPublicKey::kakaoPublicKeySet") }
        verify(exactly = 1) {
            kakaoOidcTokenVerificationHelper.getPayloadFromIdToken(any(), any(), any(), any())
        }
        verify(exactly = 0) {
            kakaoOidcIdTokenPayloadVerifier.verifyPayload(any(), any())
        }
    }

    /**
     * 테스트용 공개키 응답 생성
     */
    private fun createMockPublicKeyResponse(): OIDCPublicKeyResponse {
        val keyPair = TestJwtBuilder.keyPair
        val publicKey = keyPair.public
        val keyFactory = KeyFactory.getInstance("RSA")
        val keySpec = keyFactory.getKeySpec(publicKey, RSAPublicKeySpec::class.java)

        val modulus = Base64.getUrlEncoder().withoutPadding().encodeToString(keySpec.modulus.toByteArray())
        val exponent = Base64.getUrlEncoder().withoutPadding().encodeToString(keySpec.publicExponent.toByteArray())

        val oidcPublicKey = OIDCPublicKey(
            kid = TestJwtBuilder.KAKAO_HEADER_KID,
            kty = "RSA",
            alg = "RS256",
            use = "sig",
            n = modulus,
            e = exponent
        )

        return OIDCPublicKeyResponse(keys = listOf(oidcPublicKey))
    }
}

