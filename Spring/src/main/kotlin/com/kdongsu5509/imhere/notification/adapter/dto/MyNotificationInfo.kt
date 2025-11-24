package com.kdongsu5509.imhere.notification.adapter.dto

import jakarta.validation.constraints.NotBlank

data class MyNotificationInfo(
    @NotBlank(message = "fcmToken 정보는 필수입니다.")
    var fcmToken: String
)