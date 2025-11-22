package com.kdongsu5509.imhere.auth.adapter.out.kakao

import com.fasterxml.jackson.databind.ObjectMapper
import com.kdongsu5509.imhere.auth.adapter.out.dto.OIDCPublicKey
import com.kdongsu5509.imhere.auth.adapter.out.dto.OIDCPublicKeyResponse
import com.kdongsu5509.imhere.auth.application.port.out.OauthClientPort
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.cache.CacheManager
import org.springframework.cache.concurrent.ConcurrentMapCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.http.MediaType
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo
import org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess

@RestClientTest(KakaoOauthClient::class)
class KakaoOauthClientTest {

    @Autowired
    private lateinit var oauthClientPort: OauthClientPort

    @Autowired
    private lateinit var mockServer: MockRestServiceServer

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @TestConfiguration
    class TestConfig {
        @Bean("oidcCacheManager")
        fun testCacheManager(): CacheManager {
            return ConcurrentMapCacheManager()
        }
    }

    @Test
    @DisplayName("카카오 공개키 요청 시 정상적으로 데이터를 파싱해서 반환한다")
    fun getPublicKeyFromProvider_success() {
        // given
        val expectedUrl = "https://kauth.kakao.com/.well-known/jwks.json"
        val mockResponseJson = createMockKakaoPublickeyResp()
        mockServer.expect(requestTo(expectedUrl))
            .andRespond(withSuccess(mockResponseJson, MediaType.APPLICATION_JSON))

        // when
        val result = oauthClientPort.getPublicKeyFromProvider()

        // then
        assertThat(result).isNotNull
        assertThat(result?.keys).hasSize(1)
        assertThat(result?.keys?.get(0)?.kid).isEqualTo("kid1")

        mockServer.verify()
    }

    private fun createMockKakaoPublickeyResp(): String {
        val mockResponse = OIDCPublicKeyResponse(
            keys = listOf(
                OIDCPublicKey("kid1", "RSA", "sig", "n_value", "e_value")
            )
        )
        return objectMapper.writeValueAsString(mockResponse)
    }
}