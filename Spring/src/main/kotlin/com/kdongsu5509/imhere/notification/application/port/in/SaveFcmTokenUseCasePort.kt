package com.kdongsu5509.imhere.notification.application.port.`in`

interface SaveFcmTokenUseCasePort {
    fun save(fcmToken: String, userEmail: String)
}