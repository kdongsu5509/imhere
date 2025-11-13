//package com.kdongsu5509.imhere.auth.application.service.oidc
//
//import com.kdongsu5509.imhere.auth.adapter.out.dto.OIDCPublicKey
//import com.kdongsu5509.imhere.auth.adapter.out.dto.OIDCPublicKeyResponse
//import com.kdongsu5509.imhere.auth.application.dto.OIDCDecodePayload
//import com.kdongsu5509.imhere.auth.application.port.out.CachePort
//import com.kdongsu5509.imhere.auth.application.service.oidc.kakao.KakaoOIDCVerificationService
//import org.junit.jupiter.api.Assertions.*
//import org.junit.jupiter.api.Test
//import org.junit.jupiter.api.extension.ExtendWith // ✅ MockitoExtension 사용을 위해 추가
//import org.mockito.ArgumentMatchers.any
//import org.mockito.ArgumentMatchers.eq
//import org.mockito.InjectMocks
//import org.mockito.Mock
//import org.mockito.Mockito.times
//import org.mockito.Mockito.verify
//import org.mockito.Mockito.`when`
//import org.mockito.junit.jupiter.MockitoExtension // ✅ Mockito 초기화를 위해 추가
//import java.security.KeyFactory
//import java.security.spec.RSAPublicKeySpec
//import java.util.*
//import org.mockito.junit.jupiter.MockitoSettings
//import org.mockito.quality.Strictness
//
//@ExtendWith(MockitoExtension::class)
//@MockitoSettings(strictness = Strictness.LENIENT)
//class KakaoOIDCVerifierTest {
//
//    // Mockito로 Mock 객체 주입
//    @Mock
//    private lateinit var cachePort: CachePort
//    @Mock
//    private lateinit var kakaoOidcTokenVerificationHelper: KakaoOidcTokenVerificationHelper
//    @Mock
//    private lateinit var kakaoOidcIdTokenPayloadVerifier: KakaoOidcIdTokenPayloadVerifier
//
//    // Mock 객체들을 주입받아 테스트 대상 객체 생성
//    @InjectMocks
//    private lateinit var kakaoOIDCVerifier: KakaoOIDCVerificationService
//
//    @Test
//    fun `유효한 ID 토큰 검증 성공`() {
//        // given
//        val idToken = TestJwtBuilder.buildValidIdToken()
//        val publicKeyResponse = createMockPublicKeyResponse()
//        val expectedPayload = OIDCDecodePayload(
//            iss = "https://kauth.kakao.com",
//            aud = "bf284f33bfeba9bc59575706d0eb0e9c",
//            sub = "사용자회원번호",
//            email = "ds.ko@kakao.com"
//        )
//
//        // ✅ whenever(..).thenReturn(...)으로 Mocking
//        `when`(cachePort.find("KakaoPublicKey::kakaoPublicKeySet"))
//            .thenReturn(Optional.of(publicKeyResponse))
//
//        `when`(
//            kakaoOidcTokenVerificationHelper.getPayloadFromIdToken(
//                idToken,
//                "https://kauth.kakao.com",
//                "bf284f33bfeba9bc59575706d0eb0e9c",
//                publicKeyResponse
//            )
//        ).thenReturn(expectedPayload)
//
//        `when`(
//            kakaoOidcIdTokenPayloadVerifier.verifyPayload(
//                expectedPayload,
//                "bf284f33bfeba9bc59575706d0eb0e9c",
//            )
//        ).thenReturn(Unit)
//
//        // when & then (예외가 발생하지 않으면 성공)
//        kakaoOIDCVerifier.verifyAndReturnEmail(idToken)
//
//        // 호출 검증 (선택적)
//        verify(cachePort, times(1)).find(any())
//    }
//
//    @Test
//    fun `공개키 캐시가 비어있을 때 예외 발생`() {
//        // given
//        val idToken = TestJwtBuilder.buildValidIdToken()
//
//        // ✅ 캐시가 비어있음을 Mocking
//        `when`(cachePort.find("KakaoPublicKey::kakaoPublicKeySet")).thenReturn(null)
//
//        // when & then
//        val exception = assertThrows(SecurityException::class.java) {
//            kakaoOIDCVerifier.verifyAndReturnEmail(idToken)
//        }
//
//        assertEquals("공개키 캐시가 비어있습니다. 카카오 서버에 요청하여 초기화가 필요합니다.", exception.message)
//
//        // ✅ 호출 횟수 검증
//        verify(cachePort, times(1)).find("KakaoPublicKey::kakaoPublicKeySet")
//        verify(kakaoOidcTokenVerificationHelper, times(0)).getPayloadFromIdToken(any(), any(), any(), any())
//    }
//
//    @Test
//    fun `토큰의 issuer가 일치하지 않을 때 예외 발생`() {
//        // given
//        val idToken = TestJwtBuilder.buildValidIdToken()
//        val publicKeyResponse = createMockPublicKeyResponse()
//        val invalidPayload = OIDCDecodePayload(
//            iss = "https://invalid.issuer.com",
//            aud = "bf284f33bfeba9bc59575706d0eb0e9c",
//            sub = "사용자회원번호",
//            email = "ds.ko@kakao.com"
//        )
//
//        `when`(cachePort.find("KakaoPublicKey::kakaoPublicKeySet")).thenReturn(Optional.of(publicKeyResponse))
//        `when`(
//            kakaoOidcTokenVerificationHelper.getPayloadFromIdToken(
//                eq(idToken), eq("https://kauth.kakao.com"), eq("bf284f33bfeba9bc59575706d0eb0e9c"), eq(publicKeyResponse)
//            )
//        ).thenReturn(invalidPayload)
//
//        // ✅ 예외 발생 Mocking
//        `when`(
//            kakaoOidcIdTokenPayloadVerifier.verifyPayload(
//                eq(invalidPayload),
//                eq("bf284f33bfeba9bc59575706d0eb0e9c")
//            )
//        ).thenThrow(SecurityException("토큰의 issuer가 일치하지 않습니다. 예상: https://kauth.kakao.com, 실제: https://invalid.issuer.com"))
//
//        // when & then
//        val exception = assertThrows(SecurityException::class.java) {
//            kakaoOIDCVerifier.verifyAndReturnEmail(idToken)
//        }
//
//        assertTrue(exception.message!!.contains("토큰의 issuer가 일치하지 않습니다"))
//
//        // ✅ 호출 횟수 검증
//        verify(cachePort, times(1)).find(any())
//        verify(kakaoOidcTokenVerificationHelper, times(1)).getPayloadFromIdToken(any(), any(), any(), any())
//        verify(kakaoOidcIdTokenPayloadVerifier, times(1)).verifyPayload(any(), any())
//    }
//
//    @Test
//    fun `토큰의 audience가 일치하지 않을 때 예외 발생`() {
//        // given
//        val idToken = TestJwtBuilder.buildValidIdToken()
//        val publicKeyResponse = createMockPublicKeyResponse()
//        val invalidPayload = OIDCDecodePayload(
//            iss = "https://kauth.kakao.com",
//            aud = "invalid-audience",
//            sub = "사용자회원번호",
//            email = "ds.ko@kakao.com"
//        )
//
//        `when`(cachePort.find("KakaoPublicKey::kakaoPublicKeySet")).thenReturn(Optional.of(publicKeyResponse))
//        `when`(
//            kakaoOidcTokenVerificationHelper.getPayloadFromIdToken(
//                eq(idToken), eq("https://kauth.kakao.com"), eq("bf284f33bfeba9bc59575706d0eb0e9c"), eq(publicKeyResponse)
//            )
//        ).thenReturn(invalidPayload)
//
//        `when`(
//            kakaoOidcIdTokenPayloadVerifier.verifyPayload(
//                eq(invalidPayload),
//                eq("bf284f33bfeba9bc59575706d0eb0e9c"),
//            )
//        ).thenThrow(SecurityException("토큰의 audience가 일치하지 않습니다. 예상: bf284f33bfeba9bc59575706d0eb0e9c, 실제: invalid-audience"))
//
//        // when & then
//        val exception = assertThrows(SecurityException::class.java) {
//            kakaoOIDCVerifier.verifyAndReturnEmail(idToken)
//        }
//
//        assertTrue(exception.message!!.contains("토큰의 audience가 일치하지 않습니다"))
//
//        // ✅ 호출 횟수 검증
//        verify(cachePort, times(1)).find(any())
//        verify(kakaoOidcTokenVerificationHelper, times(1)).getPayloadFromIdToken(any(), any(), any(), any())
//        verify(kakaoOidcIdTokenPayloadVerifier, times(1)).verifyPayload(any(), any())
//    }
//
//    @Test
//    fun `KakaoOidcTokenVerificationHelper에서 예외 발생 시 SecurityException으로 변환`() {
//        // given
//        val idToken = TestJwtBuilder.buildValidIdToken()
//        val publicKeyResponse = createMockPublicKeyResponse()
//
//        `when`(cachePort.find("KakaoPublicKey::kakaoPublicKeySet")).thenReturn(Optional.of(publicKeyResponse))
//
//        // ✅ 런타임 예외 발생 Mocking
//        `when`(
//            kakaoOidcTokenVerificationHelper.getPayloadFromIdToken(
//                eq(idToken), eq("https://kauth.kakao.com"), eq("bf284f33bfeba9bc59575706d0eb0e9c"), eq(publicKeyResponse)
//            )
//        ).thenThrow(RuntimeException("토큰 검증 실패"))
//
//        // when & then
//        val exception = assertThrows(SecurityException::class.java) {
//            kakaoOIDCVerifier.verifyAndReturnEmail(idToken)
//        }
//
//        assertTrue(exception.message!!.contains("ID 토큰 검증에 실패했습니다"))
//
//        // ✅ 호출 횟수 검증
//        verify(cachePort, times(1)).find(any())
//        verify(kakaoOidcTokenVerificationHelper, times(1)).getPayloadFromIdToken(any(), any(), any(), any())
//        verify(kakaoOidcIdTokenPayloadVerifier, times(0)).verifyPayload(any(), any())
//    }
//
//    /**
//     * 테스트용 공개키 응답 생성 (수정 없음)
//     */
//    private fun createMockPublicKeyResponse(): OIDCPublicKeyResponse {
//        val keyPair = TestJwtBuilder.keyPair
//        val publicKey = keyPair.public
//        val keyFactory = KeyFactory.getInstance("RSA")
//        val keySpec = keyFactory.getKeySpec(publicKey, RSAPublicKeySpec::class.java)
//
//        val modulus = Base64.getUrlEncoder().withoutPadding().encodeToString(keySpec.modulus.toByteArray())
//        val exponent = Base64.getUrlEncoder().withoutPadding().encodeToString(keySpec.publicExponent.toByteArray())
//
//        val oidcPublicKey = OIDCPublicKey(
//            kid = TestJwtBuilder.KAKAO_HEADER_KID,
//            kty = "RSA",
//            alg = "RS256",
//            use = "sig",
//            n = modulus,
//            e = exponent
//        )
//
//        return OIDCPublicKeyResponse(keys = listOf(oidcPublicKey))
//    }
//}