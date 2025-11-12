package com.kdongsu5509.imhere.auth.adapter.out.jjwt

import com.kdongsu5509.imhere.auth.adapter.out.dto.OIDCPublicKeyResponse
import com.kdongsu5509.imhere.auth.adapter.out.kakao.KakaoOIDCProperties
import com.kdongsu5509.imhere.auth.application.port.out.CachePort
import com.kdongsu5509.imhere.auth.application.port.out.JwtParserPort
import com.kdongsu5509.imhere.common.exception.implementation.auth.KakaoOIDCKeyFetchFailFromRedisException
import org.springframework.stereotype.Component

@Component
class JjwtParserAdapter(
    private val cachePort: CachePort,
    private val kakaoOIDCProperties: KakaoOIDCProperties,
    private val kakaoOidcJwtTokenParser: KakaoOidcJwtTokenParser
): JwtParserPort {
    /**
     * TODO: JJWT 라이브러리를 사용하여 JWT 파싱 로직 구현
     * 공개키를 사용하여 JWT를 검증해야만, 안전하게 토큰을 파싱할 수 있습니다.
     */
    override fun parse(idToken: String) {

    }

    private fun getCachedPublicKeys(): OIDCPublicKeyResponse {
        val cachedKeySet = cachePort.find(kakaoOIDCProperties.cacheKey) as? OIDCPublicKeyResponse
            ?: throw KakaoOIDCKeyFetchFailFromRedisException()
        return cachedKeySet
    }
}