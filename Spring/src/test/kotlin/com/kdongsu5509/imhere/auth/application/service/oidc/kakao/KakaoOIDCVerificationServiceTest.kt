package com.kdongsu5509.imhere.auth.application.service.oidc.kakao

import com.kdongsu5509.imhere.auth.application.dto.OIDCDecodePayload
import com.kdongsu5509.imhere.auth.application.port.out.JwtParserPort
import com.kdongsu5509.imhere.auth.application.port.out.JwtVerficationPort
import io.jsonwebtoken.MalformedJwtException
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class KakaoOIDCVerificationServiceTest {

    @Mock
    private lateinit var jwtParserPort: JwtParserPort

    @Mock
    private lateinit var jwtVerficationPort: JwtVerficationPort

    private lateinit var kakaoOIDCVerificationService: KakaoOIDCVerificationService

    @org.junit.jupiter.api.BeforeEach
    fun setUp() {
        kakaoOIDCVerificationService = KakaoOIDCVerificationService(jwtParserPort, jwtVerficationPort)
    }

    @Test
    @DisplayName("유효한 ID 토큰으로 사용자 정보를 성공적으로 반환한다")
    fun verifyAndReturnUserInformation_success() {
        // given
        val idToken = "valid-id-token"
        val email = "test@example.com"
        val payload = OIDCDecodePayload(
            iss = "https://kauth.kakao.com",
            aud = "test-audience",
            sub = "test-sub",
            email = email
        )

        `when`(jwtParserPort.parse(idToken)).thenReturn(payload)
        doNothing().`when`(jwtVerficationPort).verifyPayLoad(payload)

        // when
        val result = kakaoOIDCVerificationService.verifyAndReturnUserInformation(idToken)

        // then
        assertThat(result).isNotNull()
        assertThat(result.email).isEqualTo(email)

        verify(jwtParserPort).parse(idToken)
        verify(jwtVerficationPort).verifyPayLoad(payload)
    }

    @Test
    @DisplayName("이메일이 없는 ID 토큰은 예외를 발생시킨다")
    fun verifyAndReturnUserInformation_noEmail_throwsException() {
        // given
        val idToken = "token-without-email"
        val payload = OIDCDecodePayload(
            iss = "https://kauth.kakao.com",
            aud = "test-audience",
            sub = "test-sub",
            email = null
        )

        `when`(jwtParserPort.parse(idToken)).thenReturn(payload)
        doNothing().`when`(jwtVerficationPort).verifyPayLoad(payload)

        // when & then
        assertThrows<MalformedJwtException> {
            kakaoOIDCVerificationService.verifyAndReturnUserInformation(idToken)
        }.also { exception ->
            assertThat(exception.message).isEqualTo("ID 토큰에 이메일 정보가 없습니다")
        }

        verify(jwtParserPort).parse(idToken)
        verify(jwtVerficationPort).verifyPayLoad(payload)
    }

    @Test
    @DisplayName("페이로드 검증 실패 시 예외를 전파한다")
    fun verifyAndReturnUserInformation_payloadVerificationFails_throwsException() {
        // given
        val idToken = "invalid-token"
        val payload = OIDCDecodePayload(
            iss = "https://kauth.kakao.com",
            aud = "test-audience",
            sub = "test-sub",
            email = "test@example.com"
        )
        val exception = MalformedJwtException("Invalid payload")

        `when`(jwtParserPort.parse(idToken)).thenReturn(payload)
        doThrow(exception).`when`(jwtVerficationPort).verifyPayLoad(payload)

        // when & then
        assertThrows<MalformedJwtException> {
            kakaoOIDCVerificationService.verifyAndReturnUserInformation(idToken)
        }

        verify(jwtParserPort).parse(idToken)
        verify(jwtVerficationPort).verifyPayLoad(payload)
    }
}

