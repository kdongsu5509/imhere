package com.kdongsu5509.imhere.notification.application.domain

data class FcmToken(
    val userEmail: String,
    val fcmToken: String
)
