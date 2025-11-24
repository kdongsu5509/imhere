package com.kdongsu5509.imhere.notification.application.service

import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.Notification
import com.kdongsu5509.imhere.common.exception.implementation.notification.FcmTokenNotFoundException
import com.kdongsu5509.imhere.notification.application.domain.FcmToken
import com.kdongsu5509.imhere.notification.application.port.`in`.SelfNotificationUserCasePort
import com.kdongsu5509.imhere.notification.application.port.out.FindTokenPort
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service


@Service
@Transactional
class SelfNotificationService(
    private val findTokenPort: FindTokenPort
) : SelfNotificationUserCasePort {

    private val title = "전송 완료"
    private val body = "문자 메시지 발송에 성공하였습니다"

    override fun sendToMe(email: String) {
        //1. 내 token 정보를 가져온다.
        val myFcmTokenInfo: FcmToken = findTokenPort.findByUserEmail(email)
            ?: throw FcmTokenNotFoundException()

        //2. 나에게 메세지를 보낸다.
        val fcmMessage = createFcmMessage(myFcmTokenInfo)
        FirebaseMessaging.getInstance().send(fcmMessage)
    }

    private fun createFcmMessage(
        myFcmTokenInfo: FcmToken
    ): Message? {
        return Message.builder().setNotification(
            Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build()
        )
            .setToken(myFcmTokenInfo.fcmToken)
            .build()
    }
}