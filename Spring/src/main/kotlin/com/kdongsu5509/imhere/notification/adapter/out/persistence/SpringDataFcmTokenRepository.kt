package com.kdongsu5509.imhere.notification.adapter.out.persistence

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SpringDataFcmTokenRepository : JpaRepository<FcmTokenEntity, Long> {
    fun findByUserEmail(email: String): FcmTokenEntity?
}