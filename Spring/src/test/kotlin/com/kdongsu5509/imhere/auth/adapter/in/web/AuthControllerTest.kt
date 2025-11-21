package com.kdongsu5509.imhere.auth.adapter.`in`.web

import com.fasterxml.jackson.databind.ObjectMapper
import com.kdongsu5509.imhere.auth.adapter.dto.req.TokenInfo
import com.kdongsu5509.imhere.auth.adapter.dto.resp.ImhereJwt
import com.kdongsu5509.imhere.auth.application.dto.SelfSignedJWT
import com.kdongsu5509.imhere.auth.application.port.`in`.HandleOIDCUseCase
import com.kdongsu5509.imhere.auth.application.port.`in`.ReissueJWTPort
import com.kdongsu5509.imhere.auth.domain.OAuth2Provider
import com.kdongsu5509.imhere.common.config.SecurityConfig
import com.kdongsu5509.imhere.common.exception.implementation.auth.ImHereTokenInvalidException
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.FilterType
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.web.filter.OncePerRequestFilter

@WebMvcTest(
    controllers = [AuthController::class],
    excludeAutoConfiguration = [SecurityConfig::class],
    excludeFilters = [
        ComponentScan.Filter(
            type = FilterType.ASSIGNABLE_TYPE,
            classes = [
                OncePerRequestFilter::class,
            ]
        )
    ]
)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockitoBean
    private lateinit var handleOidcUseCase: HandleOIDCUseCase

    @MockitoBean
    private lateinit var reissueJWTPort: ReissueJWTPort

    @Test
    @DisplayName("로그인 요청이 들어오고 정상 응답의 경우에는 ImhereJwt가 응답으로 나간다")
    fun login_success() {
        //given
        val loginUrl = "/api/v1/auth/login"
        val idToken = "testIdToken"
        val provider = OAuth2Provider.KAKAO
        val mockLoginReq = objectMapper.writeValueAsString(
            TokenInfo(provider = provider, idToken = idToken)
        )

        val expectedJwt = SelfSignedJWT(
            accessToken = "testAccessToken",
            refreshToken = "testRefreshToken"
        )
        `when`(handleOidcUseCase.verifyIdTokenAndReturnJwt(idToken, provider))
            .thenReturn(expectedJwt)

        //when & then
        val result = mockMvc.perform(
            post(loginUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mockLoginReq)
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn()

        val responseBody = objectMapper.readValue(
            result.response.contentAsString,
            ImhereJwt::class.java
        )

        assertThat(responseBody.accessToken).isEqualTo(expectedJwt.accessToken)
        assertThat(responseBody.refreshToken).isEqualTo(expectedJwt.refreshToken)
    }

    @Test
    @DisplayName("로그인 요청 시 유효하지 않은 ID 토큰이면 401 에러를 반환한다")
    fun login_failure_invalid_token() {
        //given
        val loginUrl = "/api/v1/auth/login"
        val idToken = "invalidIdToken"
        val provider = OAuth2Provider.KAKAO
        val mockLoginReq = objectMapper.writeValueAsString(
            TokenInfo(provider = provider, idToken = idToken)
        )

        `when`(handleOidcUseCase.verifyIdTokenAndReturnJwt(idToken, provider))
            .thenThrow(ImHereTokenInvalidException())

        //when & then
        mockMvc.perform(
            post(loginUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mockLoginReq)
                .with(csrf())
        )
            .andExpect(status().isUnauthorized)
    }

    @Test
    @DisplayName("토큰 재발급 요청이 들어오고 정상 응답의 경우에는 ImhereJwt가 응답으로 나간다")
    fun reissue_success() {
        //given
        val reissueUrl = "/api/v1/auth/reissue"
        val refreshToken = "testRefreshToken"
        val mockReissueReq = objectMapper.writeValueAsString(
            mapOf("refreshToken" to refreshToken)
        )

        val expectedJwt = SelfSignedJWT(
            accessToken = "newAccessToken",
            refreshToken = "newRefreshToken"
        )
        `when`(reissueJWTPort.reissue(refreshToken))
            .thenReturn(expectedJwt)

        //when & then
        val result = mockMvc.perform(
            post(reissueUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mockReissueReq)
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn()

        val responseBody = objectMapper.readValue(
            result.response.contentAsString,
            ImhereJwt::class.java
        )

        assertThat(responseBody.accessToken).isEqualTo(expectedJwt.accessToken)
        assertThat(responseBody.refreshToken).isEqualTo(expectedJwt.refreshToken)
    }

    @Test
    @DisplayName("토큰 재발급 요청 시 유효하지 않은 refresh 토큰이면 401 에러를 반환한다")
    fun reissue_failure_invalid_token() {
        //given
        val reissueUrl = "/api/v1/auth/reissue"
        val refreshToken = "invalidRefreshToken"
        val mockReissueReq = objectMapper.writeValueAsString(
            mapOf("refreshToken" to refreshToken)
        )

        `when`(reissueJWTPort.reissue(refreshToken))
            .thenThrow(ImHereTokenInvalidException())

        //when & then
        mockMvc.perform(
            post(reissueUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mockReissueReq)
                .with(csrf())
        )
            .andExpect(status().isUnauthorized)
    }

    @Test
    @DisplayName("로그인 요청 시 GOOGLE provider로도 정상 동작한다")
    fun login_success_with_google() {
        //given
        val loginUrl = "/api/v1/auth/login"
        val idToken = "googleIdToken"
        val provider = OAuth2Provider.GOOGLE
        val mockLoginReq = objectMapper.writeValueAsString(
            TokenInfo(provider = provider, idToken = idToken)
        )

        val expectedJwt = SelfSignedJWT(
            accessToken = "googleAccessToken",
            refreshToken = "googleRefreshToken"
        )
        `when`(handleOidcUseCase.verifyIdTokenAndReturnJwt(idToken, provider))
            .thenReturn(expectedJwt)

        //when & then
        val result = mockMvc.perform(
            post(loginUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mockLoginReq)
        )
            .andExpect(status().isOk)
            .andReturn()

        val responseBody = objectMapper.readValue(
            result.response.contentAsString,
            ImhereJwt::class.java
        )

        assertThat(responseBody.accessToken).isEqualTo(expectedJwt.accessToken)
        assertThat(responseBody.refreshToken).isEqualTo(expectedJwt.refreshToken)
    }
}