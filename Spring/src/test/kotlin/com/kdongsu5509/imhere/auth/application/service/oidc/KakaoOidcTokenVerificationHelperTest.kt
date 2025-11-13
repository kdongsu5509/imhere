package com.kdongsu5509.imhere.auth.application.service.oidc

import com.kdongsu5509.imhere.auth.adapter.out.dto.OIDCPublicKey
import com.kdongsu5509.imhere.auth.adapter.out.dto.OIDCPublicKeyResponse
import com.kdongsu5509.imhere.auth.application.dto.OIDCDecodePayload
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import java.security.KeyFactory
import java.security.spec.RSAPublicKeySpec
import java.util.*

@ExtendWith(MockitoExtension::class)
class KakaoOidcTokenVerificationHelperTest {

    @Mock
    private lateinit var kakaoOidcJwtTokenParser: KakaoOidcJwtTokenParser

    @Mock
    private lateinit var kakaoOidcJwtTokenSignatureVerifier: KakaoOidcJwtTokenSignatureVerifier

    @InjectMocks
    private lateinit var kakaoOidcTokenVerificationHelper: KakaoOidcTokenVerificationHelper

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

        `when`(kakaoOidcJwtTokenParser.getKidFromOriginTokenHeader(idToken, iss, aud))
            .thenReturn(expectedKid)
        `when`(kakaoOidcJwtTokenSignatureVerifier.verifyTokenSignature(idToken, oidcPublicKey.n, oidcPublicKey.e))
            .thenReturn(mockJws)
        `when`(kakaoOidcJwtTokenParser.extractPayloadFromJws(mockJws))
            .thenReturn(expectedPayload)

        // when
        val result = kakaoOidcTokenVerificationHelper.getPayloadFromIdToken(idToken, iss, aud, publicKeyResponse)

        // then
        assertEquals(expectedPayload, result)
    }

    @Test
    fun `키 목록에 해당하는 kid가 없을 때 예외 발생`() {
        // given
        val idToken = TestJwtBuilder.buildValidIdToken()
        val iss = "https://kauth.kakao.com"
        val aud = "bf284f33bfeba9bc59575706d0eb0e9c"
        val publicKeyResponse = createMockPublicKeyResponse()
        val invalidKid = "invalid-kid"

        `when`(
            kakaoOidcJwtTokenParser.getKidFromOriginTokenHeader(idToken, iss, aud)
        ).thenReturn(invalidKid)

        // when & then
        val exception = assertThrows(SecurityException::class.java) {
            kakaoOidcTokenVerificationHelper.getPayloadFromIdToken(idToken, iss, aud, publicKeyResponse)
        }

        assertTrue(exception.message!!.contains("캐시된 키 목록에 kid: $invalidKid 에 해당하는 키가 없습니다"))


        verify(kakaoOidcJwtTokenParser, times(1)).getKidFromOriginTokenHeader(String(), String(), String())
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

        `when`(
            kakaoOidcJwtTokenParser.getKidFromOriginTokenHeader(idToken, iss, aud)
        ).thenReturn(TestJwtBuilder.KAKAO_HEADER_KID)

        `when`(
            kakaoOidcJwtTokenParser.extractPayloadFromJws(mockJws)
        ).thenReturn(expectedPayload)

        `when`(
            kakaoOidcJwtTokenParser.extractPayloadFromJws(mockJws)
        ).thenReturn(expectedPayload)

        // when
        val result = kakaoOidcTokenVerificationHelper.getPayloadFromIdToken(idToken, iss, aud, publicKeyResponse)

        // then
        assertEquals(expectedPayload, result)
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

