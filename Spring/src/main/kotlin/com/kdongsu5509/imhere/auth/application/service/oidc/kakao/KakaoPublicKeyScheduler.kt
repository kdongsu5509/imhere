package com.kdongsu5509.imhere.auth.application.service.oidc.kakao

import com.kdongsu5509.imhere.auth.application.port.out.OauthClientPort
import jakarta.annotation.PostConstruct
import lombok.extern.slf4j.Slf4j
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Slf4j
@Component
class KakaoPublicKeyScheduler(
    val oauthClientPort: OauthClientPort,
) {
    private val log = LoggerFactory.getLogger(KakaoPublicKeyScheduler::class.java)

    companion object {
        private const val DURATION: Long = 7 * 24 * 60 * 60 * 1000 // 7일 (밀리초)
    }

    @PostConstruct
    fun initializePublicKeyCache() {
        log.info("카카오 공개키 초기화 시작")
        oauthClientPort.getPublicKeyFromProvider()
        log.info("카카오 공개키 초기화 종료")
    }

    @Scheduled(fixedRate = DURATION)
    fun updatePublicKey() {
        oauthClientPort.getPublicKeyFromProvider()
    }
}