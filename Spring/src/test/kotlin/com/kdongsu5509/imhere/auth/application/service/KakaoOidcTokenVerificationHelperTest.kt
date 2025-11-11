package com.kdongsu5509.imhere.auth.application.service

import com.kdongsu5509.imhere.auth.adapter.out.dto.OIDCPublicKey
import com.kdongsu5509.imhere.auth.adapter.out.dto.OIDCPublicKeyResponse
import com.kdongsu5509.imhere.auth.application.dto.OIDCDecodePayload
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigInteger
import java.security.KeyFactory
import java.security.spec.RSAPublicKeySpec
import java.util.*

class KakaoOidcTokenVerificationHelperTest {

    private lateinit var kakaoOidcJwtTokenParser: KakaoOidcJwtTokenParser
    private lateinit var kakaoOidcJwtTokenSignatureVerifier: KakaoOidcJwtTokenSignatureVerifier
    private lateinit var kakaoOidcTokenVerificationHelper: KakaoOidcTokenVerificationHelper

    @BeforeEach
    fun setUp() {
        kakaoOidcJwtTokenParser = mockk()
        kakaoOidcJwtTokenSignatureVerifier = mockk()
        kakaoOidcTokenVerificationHelper = KakaoOidcTokenVerificationHelper(
            kakaoOidcJwtTokenParser,
            kakaoOidcJwtTokenSignatureVerifier
        )
    }

    @Test
    fun `유효한 ID 토큰으로부터 페이로드 추출 성공`() {
        // given
        val idToken = TestJwtBuilder.buildValidIdToken()
        val iss = "https://kauth.kakao.com"
        val aud = "bf284f33bfeba9bc59575706d0eb0e9c"
        val publicKeyResponse = createMockPublicKeyResponse()
        val expectedKid = TestJwtBuilder.KAKAO_HEADER_KID
        val expectedPayload = OIDCDecodePayload(
            iss = iss,
            aud = aud,
            sub = "사용자회원번호",
            email = "ds.ko@kakao.com"
        )

        val oidcPublicKey = publicKeyResponse.keys.first()
        val mockJws = io.jsonwebtoken.Jwts.parserBuilder()
            .setSigningKey(TestJwtBuilder.testPublicKey)
            .build()
            .parseClaimsJws(idToken)

        every {
            kakaoOidcJwtTokenParser.getKidFromUnsignedTokenHeader(idToken, iss, aud)
        } returns expectedKid
        every {
            kakaoOidcJwtTokenSignatureVerifier.verifyTokenSignature(idToken, oidcPublicKey.n, oidcPublicKey.e)
        } returns mockJws
        every {
            kakaoOidcJwtTokenParser.extractPayloadFromJws(mockJws)
        } returns expectedPayload

        // when
        val result = kakaoOidcTokenVerificationHelper.getPayloadFromIdToken(idToken, iss, aud, publicKeyResponse)

        // then
        assertEquals(expectedPayload, result)
        verify(exactly = 1) {
            kakaoOidcJwtTokenParser.getKidFromUnsignedTokenHeader(idToken, iss, aud)
        }
        verify(exactly = 1) {
            kakaoOidcJwtTokenSignatureVerifier.verifyTokenSignature(idToken, oidcPublicKey.n, oidcPublicKey.e)
        }
        verify(exactly = 1) {
            kakaoOidcJwtTokenParser.extractPayloadFromJws(mockJws)
        }
    }

    @Test
    fun `키 목록에 해당하는 kid가 없을 때 예외 발생`() {
        // given
        val idToken = TestJwtBuilder.buildValidIdToken()
        val iss = "https://kauth.kakao.com"
        val aud = "bf284f33bfeba9bc59575706d0eb0e9c"
        val publicKeyResponse = createMockPublicKeyResponse()
        val invalidKid = "invalid-kid"

        every {
            kakaoOidcJwtTokenParser.getKidFromUnsignedTokenHeader(idToken, iss, aud)
        } returns invalidKid

        // when & then
        val exception = assertThrows(SecurityException::class.java) {
            kakaoOidcTokenVerificationHelper.getPayloadFromIdToken(idToken, iss, aud, publicKeyResponse)
        }

        assertTrue(exception.message!!.contains("캐시된 키 목록에 kid: $invalidKid 에 해당하는 키가 없습니다"))
        verify(exactly = 1) {
            kakaoOidcJwtTokenParser.getKidFromUnsignedTokenHeader(idToken, iss, aud)
        }
        verify(exactly = 0) {
            kakaoOidcJwtTokenSignatureVerifier.verifyTokenSignature(any(), any(), any())
        }
        verify(exactly = 0) {
            kakaoOidcJwtTokenParser.extractPayloadFromJws(any())
        }
    }

    @Test
    fun `여러 개의 키 중 올바른 kid를 가진 키를 찾아 사용`() {
        // given
        val idToken = TestJwtBuilder.buildValidIdToken()
        val iss = "https://kauth.kakao.com"
        val aud = "bf284f33bfeba9bc59575706d0eb0e9c"
        
        val keyPair = TestJwtBuilder.keyPair
        val publicKey = keyPair.public
        val keyFactory = KeyFactory.getInstance("RSA")
        val keySpec = keyFactory.getKeySpec(publicKey, RSAPublicKeySpec::class.java)
        val modulus = Base64.getUrlEncoder().withoutPadding().encodeToString(keySpec.modulus.toByteArray())
        val exponent = Base64.getUrlEncoder().withoutPadding().encodeToString(keySpec.publicExponent.toByteArray())

        // 여러 개의 키 생성
        val key1 = OIDCPublicKey(
            kid = "wrong-kid-1",
            kty = "RSA",
            alg = "RS256",
            use = "sig",
            n = modulus,
            e = exponent
        )
        val key2 = OIDCPublicKey(
            kid = TestJwtBuilder.KAKAO_HEADER_KID, // 올바른 kid
            kty = "RSA",
            alg = "RS256",
            use = "sig",
            n = modulus,
            e = exponent
        )
        val key3 = OIDCPublicKey(
            kid = "wrong-kid-3",
            kty = "RSA",
            alg = "RS256",
            use = "sig",
            n = modulus,
            e = exponent
        )

        val publicKeyResponse = OIDCPublicKeyResponse(keys = listOf(key1, key2, key3))
        val expectedPayload = OIDCDecodePayload(
            iss = iss,
            aud = aud,
            sub = "사용자회원번호",
            email = "ds.ko@kakao.com"
        )

        val mockJws = io.jsonwebtoken.Jwts.parserBuilder()
            .setSigningKey(TestJwtBuilder.testPublicKey)
            .build()
            .parseClaimsJws(idToken)

        every {
            kakaoOidcJwtTokenParser.getKidFromUnsignedTokenHeader(idToken, iss, aud)
        } returns TestJwtBuilder.KAKAO_HEADER_KID
        every {
            kakaoOidcJwtTokenSignatureVerifier.verifyTokenSignature(idToken, key2.n, key2.e)
        } returns mockJws
        every {
            kakaoOidcJwtTokenParser.extractPayloadFromJws(mockJws)
        } returns expectedPayload

        // when
        val result = kakaoOidcTokenVerificationHelper.getPayloadFromIdToken(idToken, iss, aud, publicKeyResponse)

        // then
        assertEquals(expectedPayload, result)
        verify(exactly = 1) {
            kakaoOidcJwtTokenParser.getKidFromUnsignedTokenHeader(idToken, iss, aud)
        }
        verify(exactly = 1) {
            kakaoOidcJwtTokenSignatureVerifier.verifyTokenSignature(idToken, key2.n, key2.e)
        }
        verify(exactly = 1) {
            kakaoOidcJwtTokenParser.extractPayloadFromJws(mockJws)
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

