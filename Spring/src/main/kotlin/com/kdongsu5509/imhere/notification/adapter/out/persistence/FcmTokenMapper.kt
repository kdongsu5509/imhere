package com.kdongsu5509.imhere.notification.adapter.out.persistence

import com.kdongsu5509.imhere.auth.adapter.out.persistence.UserJpaEntity
import com.kdongsu5509.imhere.notification.application.domain.FcmToken
import org.springframework.stereotype.Component

@Component
class FcmTokenMapper {
    fun mapToJpaEntity(fcmToken: FcmToken, user: UserJpaEntity): FcmTokenEntity {
        return FcmTokenEntity(
            token = fcmToken.fcmToken,
            user = user
        )
    }

    fun mapToDomainEntity(jpaEntity: FcmTokenEntity): FcmToken {
        return FcmToken(
            userEmail = jpaEntity.user.email,
            fcmToken = jpaEntity.token
        )
    }
}