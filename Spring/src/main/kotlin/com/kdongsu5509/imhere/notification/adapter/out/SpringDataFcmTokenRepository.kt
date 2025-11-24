package com.kdongsu5509.imhere.notification.adapter.out

import org.springframework.data.jpa.repository.JpaRepository

interface SpringDataFcmTokenRepository : JpaRepository<FcmTokenEntity, Long> {
    fun findByUserEmail(email: String): FcmTokenEntity?
}