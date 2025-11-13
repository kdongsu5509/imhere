//package com.kdongsu5509.imhere.auth.application.service.oidc
//
//import com.kdongsu5509.imhere.auth.application.dto.OIDCDecodePayload
//import org.junit.jupiter.api.Assertions.assertThrows
//import org.junit.jupiter.api.Assertions.assertTrue
//import org.junit.jupiter.api.BeforeEach
//import org.junit.jupiter.api.Test
//
//class KakaoOidcIdTokenPayloadVerifierTest {
//
//    private lateinit var kakaoOidcIdTokenPayloadVerifier: KakaoOidcIdTokenPayloadVerifier
//
//    @BeforeEach
//    fun setUp() {
//        kakaoOidcIdTokenPayloadVerifier = KakaoOidcIdTokenPayloadVerifier()
//    }
//
//    @Test
//    fun `유효한 페이로드 검증 성공`() {
//        // given
//        val payload = OIDCDecodePayload(
//            iss = "https://kauth.kakao.com",
//            aud = "bf284f33bfeba9bc59575706d0eb0e9c",
//            sub = "사용자회원번호",
//            email = "ds.ko@kakao.com"
//        )
//        val expectedAudience = "bf284f33bfeba9bc59575706d0eb0e9c"
//
//        // when & then (예외가 발생하지 않으면 성공)
//        kakaoOidcIdTokenPayloadVerifier.verifyPayload(payload, expectedAudience)
//    }
//
//    @Test
//    fun `issuer가 일치하지 않을 때 예외 발생`() {
//        // given
//        val payload = OIDCDecodePayload(
//            iss = "https://invalid.issuer.com", // 잘못된 issuer
//            aud = "bf284f33bfeba9bc59575706d0eb0e9c",
//            sub = "사용자회원번호",
//            email = "ds.ko@kakao.com"
//        )
//        val expectedAudience = "bf284f33bfeba9bc59575706d0eb0e9c"
//
//        // when & then
//        val exception = assertThrows(SecurityException::class.java) {
//            kakaoOidcIdTokenPayloadVerifier.verifyPayload(payload, expectedAudience)
//        }
//
//        assertTrue(exception.message!!.contains("토큰의 issuer가 일치하지 않습니다"))
//        assertTrue(exception.message!!.contains("https://kauth.kakao.com"))
//        assertTrue(exception.message!!.contains("https://invalid.issuer.com"))
//    }
//
//    @Test
//    fun `audience가 일치하지 않을 때 예외 발생`() {
//        // given
//        val payload = OIDCDecodePayload(
//            iss = "https://kauth.kakao.com",
//            aud = "invalid-audience", // 잘못된 audience
//            sub = "사용자회원번호",
//            email = "ds.ko@kakao.com"
//        )
//        val expectedAudience = "bf284f33bfeba9bc59575706d0eb0e9c"
//
//        // when & then
//        val exception = assertThrows(SecurityException::class.java) {
//            kakaoOidcIdTokenPayloadVerifier.verifyPayload(payload, expectedAudience)
//        }
//
//        assertTrue(exception.message!!.contains("토큰의 audience가 일치하지 않습니다"))
//        assertTrue(exception.message!!.contains("bf284f33bfeba9bc59575706d0eb0e9c"))
//        assertTrue(exception.message!!.contains("invalid-audience"))
//    }
//
//    @Test
//    fun `nonce가 null이면 nonce 검증을 건너뜀`() {
//        // given
//        val payload = OIDCDecodePayload(
//            iss = "https://kauth.kakao.com",
//            aud = "bf284f33bfeba9bc59575706d0eb0e9c",
//            sub = "사용자회원번호",
//            email = "ds.ko@kakao.com"
//        )
//        val expectedAudience = "bf284f33bfeba9bc59575706d0eb0e9c"
//
//        // when & then (예외가 발생하지 않으면 성공)
//        kakaoOidcIdTokenPayloadVerifier.verifyPayload(payload, expectedAudience)
//    }
//}
//
