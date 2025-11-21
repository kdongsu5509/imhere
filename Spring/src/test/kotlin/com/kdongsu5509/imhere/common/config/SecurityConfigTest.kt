package com.kdongsu5509.imhere.common.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.kdongsu5509.imhere.TestJwtBuilder
import com.kdongsu5509.imhere.auth.adapter.`in`.web.AuthController
import com.kdongsu5509.imhere.auth.application.dto.SelfSignedJWT
import com.kdongsu5509.imhere.auth.application.port.`in`.HandleOIDCUseCase
import com.kdongsu5509.imhere.auth.application.port.`in`.ReissueJWTPort
import com.kdongsu5509.imhere.auth.application.service.JwtAuthenticationFilter
import com.kdongsu5509.imhere.auth.application.service.jwt.JwtTokenUtil
import com.kdongsu5509.imhere.auth.domain.OAuth2Provider
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(controllers = [AuthController::class])
@Import(SecurityConfig::class, JwtAuthenticationFilter::class)
class SecurityConfigTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    // ObjectMapper를 주입받아 JSON 직렬화에 사용하는 것이 더 안전합니다.
    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockitoBean
    private lateinit var handleOIDCUseCase: HandleOIDCUseCase

    @MockitoBean
    private lateinit var reissueJwtPort: ReissueJWTPort

    @MockitoBean
    private lateinit var jwtTokenUtil: JwtTokenUtil

    @Test
    @DisplayName("login api는 인증 필요 없음")
    fun authAPI_no_auth() {
        // given
        val testToken = TestJwtBuilder.buildValidIdToken()
        val mockRequest = createMockLoginRequest(testToken)

        val fakeResponse = SelfSignedJWT(
            "testAccessToken",
            "testRefreshToken"
        )
        `when`(handleOIDCUseCase.verifyIdTokenAndReturnJwt(testToken, OAuth2Provider.KAKAO))
            .thenReturn(fakeResponse)

        //when, then
        mockMvc.perform(
            post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mockRequest)
        )
            .andExpect(status().isOk)
    }

    @Test
    @DisplayName("토큰 재발급 api는 인증 필요 없음")
    fun reissueAPI_no_auth() {
        //given
        val testRefreshToken = "TestRefreshToken"
        val mockRequestJson = objectMapper.writeValueAsString(
            mapOf(
                "refreshToken" to testRefreshToken
            )
        )

        val fakeResponse = SelfSignedJWT(
            "testAccessToken",
            "testRefreshToken"
        )
        `when`(reissueJwtPort.reissue(testRefreshToken)).thenReturn(fakeResponse)

        // when & then
        mockMvc.perform(
            post("/api/v1/auth/reissue")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mockRequestJson)
        )
            .andDo(print())
            .andExpect(status().isOk)
    }

    @Test
    @DisplayName("인증이 필요한 엔드포인트에 토큰 없이 접근시 403 응답")
    fun secured_api_needs_auth() {
        mockMvc.perform(get("/NOT_EXIST_API"))
            .andExpect(status().isForbidden)
    }

    private fun createMockLoginRequest(testToken: String): String {
        val requestMap = mapOf(
            "provider" to "KAKAO",
            "idToken" to testToken
        )
        return objectMapper.writeValueAsString(requestMap)
    }
}