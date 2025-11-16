package com.kdongsu5509.imhere.auth.application.service.jwt

import com.kdongsu5509.imhere.auth.application.dto.SelfSignedJWT
import com.kdongsu5509.imhere.auth.application.port.`in`.ReissueJWTPort
import com.kdongsu5509.imhere.common.exception.implementation.auth.ImHereTokenExpiredException
import com.kdongsu5509.imhere.common.exception.implementation.auth.ImHereTokenInvalidException
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.stereotype.Component

@ExtendWith(MockitoExtension::class)
class JwtReissueServiceTest {

    @Mock
    lateinit var jwtTokenProvider: JwtTokenProvider

    @InjectMocks
    lateinit var jwtReissueService: JwtReissueService

    @Test
    @DisplayName("재발급 요청이 들어오면 새로운 토큰을 발급해준다.")
    fun reissue() {
        //given
        val mockRefreshToken = "prevRefreshToken"
        val mockNewSelfSignedJWT = SelfSignedJWT("accessToken", mockRefreshToken)
        `when`(jwtTokenProvider.reissueJwtToken(mockRefreshToken)).thenReturn(mockNewSelfSignedJWT)

        //when
        val result = jwtReissueService.reissue(mockRefreshToken)

        //then
        assertThat(result).isNotNull
        assertEquals(mockNewSelfSignedJWT, result)
    }

    @Test
    @DisplayName("jwtTokenProvider에서 오류가 발생하면 그 오류를 다시 반환한다")
    fun thorw_exception_of_sub_class() {
        val mockRefreshToken = "prevRefreshToken"
        val mockException = ImHereTokenInvalidException()
        `when`(jwtTokenProvider.reissueJwtToken(mockRefreshToken)).thenThrow(mockException)

        assertThrows<ImHereTokenInvalidException> {
            jwtReissueService.reissue(mockRefreshToken)
        }.also { exception ->
            assertThat(exception.message).isEqualTo("잘못된 토큰입니다")
        }

    }
}
