package com.kdongsu5509.imhere.notification.adapter.out.persistence

import com.kdongsu5509.imhere.auth.adapter.out.persistence.SpringDataUserRepository
import com.kdongsu5509.imhere.common.exception.implementation.auth.UserNotFoundException
import com.kdongsu5509.imhere.notification.application.domain.FcmToken
import com.kdongsu5509.imhere.notification.application.port.out.FindTokenPort
import com.kdongsu5509.imhere.notification.application.port.out.SaveTokenPersistencePort
import org.springframework.stereotype.Component

@Component
class FcmTokenPersistenceAdapter(
    private val fcmTokenMapper: FcmTokenMapper,
    private val springDataFcmTokenRepository: SpringDataFcmTokenRepository,
    private val springDataUserRepository: SpringDataUserRepository,
) : SaveTokenPersistencePort, FindTokenPort {

    override fun save(fcmToken: String, userEmail: String) {
        val existingEntity = springDataFcmTokenRepository.findByUserEmail(userEmail)

        if (existingEntity != null) {
            if (existingEntity.token != fcmToken) {
                existingEntity.updateToken(fcmToken)
            }
        } else {
            val user = springDataUserRepository.findByEmail(userEmail) ?: throw UserNotFoundException()

            val newEntity = FcmTokenEntity(
                token = fcmToken,
                user = user
            )
            springDataFcmTokenRepository.save(newEntity)
        }
    }

    override fun findByUserEmail(userEmail: String): FcmToken? {
        return springDataFcmTokenRepository.findByUserEmail(userEmail)
            ?.let { fcmTokenMapper.mapToDomainEntity(it) }
    }
}