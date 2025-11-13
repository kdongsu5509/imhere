package com.kdongsu5509.imhere.auth.adapter.out.jjwt

import com.kdongsu5509.imhere.auth.adapter.out.dto.OIDCPublicKey
import com.kdongsu5509.imhere.auth.application.port.out.JwtVerficationPort
import com.kdongsu5509.imhere.auth.application.port.out.LoadPublicKeyPort
import com.kdongsu5509.imhere.auth.application.service.oidc.TestJwtBuilder
import com.kdongsu5509.imhere.common.exception.implementation.auth.KakaoOIDCPublicKeyNotFoundException
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jws
import io.jsonwebtoken.MalformedJwtException
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class JjwtParserAdapterTest {

    @Mock
    private lateinit var loadPublicKeyPort: LoadPublicKeyPort

    @Mock
    private lateinit var kakaoOIDCProperties: KakaoOIDCProperties

    @Mock
    private lateinit var jwtVerficationPort: JwtVerficationPort

    @Mock
    private lateinit var mockClaims: Claims

    @Mock
    private lateinit var mockJws: Jws<Claims>

    // Spy 객체를 생성하는 헬퍼 함수
    private fun setupSpyAdapter(): JjwtParserAdapter {
        return spy(JjwtParserAdapter(loadPublicKeyPort, kakaoOIDCProperties, jwtVerficationPort))
    }

    // --- 성공 테스트 ---

    @Test
    @DisplayName("유효한 OIDC 토큰을 주입하면 Payload를 성공적으로 디코딩하여 반환한다.")
    fun success() {
        // given
        val idToken = TestJwtBuilder.buildValidIdToken()
        val expectedKid = "Test_KID"
        val expectedN = "Test_N_Modulus"
        val expectedE = "Test_E_Exponent"

        val spyAdapter = setupSpyAdapter()

        // Spy 사용: private 메서드의 반환값 고정
        doReturn(expectedKid).`when`(spyAdapter).getKidFromOriginTokenHeader(idToken)

        val mockPublicKey =
            OIDCPublicKey(kid = expectedKid, n = expectedN, e = expectedE, kty = "RSA", alg = "RS256", use = "sig")

        // Port Mocking: 공개키 로드 성공
        `when`(loadPublicKeyPort.loadPublicKey(expectedKid)).thenReturn(mockPublicKey)

        // Port Mocking: 서명 검증 성공 (Mock Jws 객체 반환)
        `when`(jwtVerficationPort.verifySignature(idToken, expectedN, expectedE)).thenReturn(mockJws)

        // Jws 객체 내부의 Payload (Claims) Mocking
        `when`(mockJws.body).thenReturn(mockClaims)
        `when`(mockClaims.issuer).thenReturn(TestJwtBuilder.KAKAO_PAYLOAD_ISS)
        `when`(mockClaims.audience).thenReturn(TestJwtBuilder.KAKAO_PAYLOAD_AUD)
        `when`(mockClaims.subject).thenReturn("TEST-SUB-ID")
        `when`(mockClaims.get("email", String::class.java)).thenReturn("test@email.com")

        // when
        val result = spyAdapter.parse(idToken)

        // then
        assertThat(result.iss).isEqualTo(TestJwtBuilder.KAKAO_PAYLOAD_ISS)
        assertThat(result.email).isEqualTo("test@email.com")

        // Port 호출 검증
        verify(loadPublicKeyPort).loadPublicKey(expectedKid)
        verify(jwtVerficationPort).verifySignature(idToken, expectedN, expectedE)
    }

    // --- 실패 테스트 1: 토큰 형식 오류 ---

    @Test
    @DisplayName("토큰 형식이 올바르지 않으면 SecurityException을 던진다 (header.payload.signature 아님)")
    fun parse_fail_invalid_token_format() {
        // given
        // Spy를 사용하지 않는 인스턴스를 사용해 getUnsignedToken의 로직을 그대로 실행
        val jjwtParserAdapter = JjwtParserAdapter(loadPublicKeyPort, kakaoOIDCProperties, jwtVerficationPort)
        val invalidToken = "header.payload" // 서명 없음

        // when & then
        assertThrows<SecurityException> {
            jjwtParserAdapter.parse(invalidToken)
        }

        // verify: Port 호출이 없었음을 검증
        verify(loadPublicKeyPort, never()).loadPublicKey(anyString())
    }

    // --- 실패 테스트 2: 공개키 로드 실패 ---

    @Test
    @DisplayName("Kid로 공개키를 찾지 못하면 KakaoOIDCPublicKeyNotFoundException을 던진다.")
    fun parse_fail_public_key_not_found() {
        // given
        val idToken = "token.to.mock"
        val notExistKid = "non-exist-kid"

        val spyAdapter = setupSpyAdapter()

        // Spy 사용: Kid 추출 성공
        doReturn(notExistKid).`when`(spyAdapter).getKidFromOriginTokenHeader(idToken)

        // Port Mocking: loadPublicKeyPort가 예외를 던지도록 설정
        `when`(loadPublicKeyPort.loadPublicKey(notExistKid))
            .thenThrow(KakaoOIDCPublicKeyNotFoundException())

        // when & then
        assertThrows<KakaoOIDCPublicKeyNotFoundException> {
            spyAdapter.parse(idToken)
        }

        // verify: loadPublicKeyPort까지는 호출, verifySignature는 호출되지 않음
        verify(loadPublicKeyPort).loadPublicKey(notExistKid)
        verify(jwtVerficationPort, never()).verifySignature(anyString(), anyString(), anyString())
    }

    // --- 실패 테스트 3: 서명 검증 실패 ---

    @Test
    @DisplayName("서명 검증에 실패하면 JwtVerficationPort 예외(예: MalformedJwtException)를 그대로 던진다.")
    fun parse_fail_signature_verification() {
        // given
        val idToken = "token.to.verify"
        val mockKid = "valid-kid"
        val expectedN = "N_Modulus"
        val expectedE = "E_Exponent"

        val mockPublicKey =
            OIDCPublicKey(kid = mockKid, n = expectedN, e = expectedE, kty = "RSA", alg = "RS256", use = "sig")

        val spyAdapter = setupSpyAdapter()

        // Spy 사용: Kid 추출 성공
        doReturn(mockKid).`when`(spyAdapter).getKidFromOriginTokenHeader(idToken)

        // Port Mocking: 공개키 로드 성공
        `when`(loadPublicKeyPort.loadPublicKey(mockKid)).thenReturn(mockPublicKey)

        // Port Mocking: JwtVerficationPort가 서명 실패 예외를 던지도록 설정
        `when`(jwtVerficationPort.verifySignature(idToken, expectedN, expectedE))
            .thenThrow(MalformedJwtException("Invalid signature or structure"))

        // when & then
        assertThrows<MalformedJwtException> {
            spyAdapter.parse(idToken)
        }

        // verify: 두 Port 모두 호출되었는지 확인
        verify(loadPublicKeyPort).loadPublicKey(mockKid)
        verify(jwtVerficationPort).verifySignature(idToken, expectedN, expectedE)
    }
}