package com.kdongsu5509.imhere.auth.adapter.out.kakao

import com.kdongsu5509.imhere.auth.adapter.out.dto.OIDCPublicKeyResponse
import com.kdongsu5509.imhere.auth.application.port.out.OauthClientPort
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

@Component
class KakaoOauthClient(private val restClientBuilder: RestClient.Builder) : OauthClientPort {
    private val kakaoBaseUrl = "https://kauth.kakao.com"
    private val publicKeyRequestSpecificUrl: String = "/.well-known/jwks.json"

    private val log = LoggerFactory.getLogger(OauthClientPort::class.java)

    @Cacheable(value = ["kakaoOidcKeys"], cacheManager = "oidcCacheManager", key = "'kakaoPublicKeySet'")
    override fun getPublicKeyFromProvider(): OIDCPublicKeyResponse? {
        val webClient = restClientBuilder.baseUrl(kakaoBaseUrl).build()

        log.info("카카오 공개키 요청 준비 완료")
        val kakaoKeySet: OIDCPublicKeyResponse? = webClient.get()
            .uri(publicKeyRequestSpecificUrl)
            .retrieve()
            .body(OIDCPublicKeyResponse::class.java)

        log.info("카카오 공개키 요청 및 캐싱 완료")
        return kakaoKeySet
    }
}