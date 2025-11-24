package com.kdongsu5509.imhere.notification.application.service

import com.kdongsu5509.imhere.notification.application.port.`in`.SaveFcmTokenUseCasePort
import com.kdongsu5509.imhere.notification.application.port.out.SaveTokenPersistencePort
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
@Transactional
class SaveFcmTokenService(
    private val saveTokenPersistencePort: SaveTokenPersistencePort,
) : SaveFcmTokenUseCasePort {
    override fun save(fcmToken: String, userEmail: String) {
        saveTokenPersistencePort.save(fcmToken, userEmail)
    }
}