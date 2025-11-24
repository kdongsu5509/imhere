package com.kdongsu5509.imhere.notification.adapter.`in`

import com.kdongsu5509.imhere.common.resp.APIResponse
import com.kdongsu5509.imhere.notification.adapter.dto.MyNotificationInfo
import com.kdongsu5509.imhere.notification.application.port.`in`.SaveFcmTokenUseCasePort
import com.kdongsu5509.imhere.notification.application.port.`in`.SelfNotificationUserCasePort
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/notification")
class NotificationController(
    private val saveFcmTokenUseCasePort: SaveFcmTokenUseCasePort,
    private val selfNotificationUserCasePort: SelfNotificationUserCasePort
) {

    @PostMapping("/enroll")
    fun enroll(
        @Validated @RequestBody myNotificationInfo: MyNotificationInfo,
        @AuthenticationPrincipal user: UserDetails
    ): APIResponse<Unit> {
        saveFcmTokenUseCasePort.save(myNotificationInfo.fcmToken, user.username)
        return APIResponse.success()
    }

    @PostMapping("/self")
    fun notifySelf(
        @AuthenticationPrincipal user: UserDetails
    ): APIResponse<Unit> {
        selfNotificationUserCasePort.sendToMe(user.username)
        return APIResponse.success()
    }
}