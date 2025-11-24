package com.kdongsu5509.imhere.notification.adapter.dto

import jakarta.validation.constraints.NotBlank
import org.jetbrains.annotations.NotNull

data class NotificationRequestDto(
    @NotNull("targetUser의 Id는 필수입니다")
    private val targetUserId: Long,
    @NotBlank(message = "알람 제목은 필수입니다")
    private val title: String,
    @NotBlank(message = "알람 내용은 필수입니다")
    private val body: String
)
