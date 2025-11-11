package com.kdongsu5509.imhere.auth.application.service.oidc

import com.kdongsu5509.imhere.auth.application.port.out.OauthClientPort
import jakarta.annotation.PostConstruct
import org.apache.commons.logging.LogFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class PublicKeyScheduler(
    val oauthClientPort: OauthClientPort,
) {
    private val log = LogFactory.getLog(PublicKeyScheduler::class.java)

    companion object {
        private const val DURATION: Long = 7 * 24 * 60 * 60 * 1000 // 7일 (밀리초)
    }

    @PostConstruct
    fun initializePublicKeyCache() {
        log.info("카카오 공개키 초기화 시작")
        oauthClientPort.getPublicKeyFromProivder()
        log.info("카카오 공개키 초기화 종료")
    }

    @Scheduled(fixedRate = DURATION)
    fun updatePublicKey() {
        oauthClientPort.getPublicKeyFromProivder()
    }
}