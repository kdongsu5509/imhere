package com.kdongsu5509.imhere.message.application.port

import com.kdongsu5509.imhere.message.adapter.dto.MessageSendRequest

interface SingleMessageSendUseCasePort {
    fun send(messageSendRequest: MessageSendRequest, email : String)
}