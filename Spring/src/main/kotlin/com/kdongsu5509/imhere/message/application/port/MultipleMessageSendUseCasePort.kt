package com.kdongsu5509.imhere.message.application.port

import com.kdongsu5509.imhere.message.adapter.dto.MultipleMessageSendRequest

interface MultipleMessageSendUseCasePort {
    fun send(multipleMessageSendRequest: MultipleMessageSendRequest, email : String)
}