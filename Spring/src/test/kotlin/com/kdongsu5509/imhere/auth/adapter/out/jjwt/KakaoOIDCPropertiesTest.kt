package com.kdongsu5509.imhere.auth.adapter.out.jjwt

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import kotlin.test.Test

@SpringBootTest
class KakaoOIDCPropertiesTest {

    @Autowired
    private lateinit var kakaoOIDCProperties: KakaoOIDCProperties

    @Test
    @DisplayName("카카오톡 정보를 application.yml에서 잘 가져온다")
    fun test() {
        assertThat(kakaoOIDCProperties.issuer).isEqualTo("https://kauth.kakao.com")
        assertThat(kakaoOIDCProperties.audience).isEqualTo("bf284f33bfeba9bc59575706d0eb0e9c")
        assertThat(kakaoOIDCProperties.cacheKey).isEqualTo("kakaoOidcKeys::kakaoPublicKeySet")
    }
}