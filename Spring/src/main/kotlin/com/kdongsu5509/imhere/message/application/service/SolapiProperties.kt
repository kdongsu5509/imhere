package com.kdongsu5509.imhere.message.application.service

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "solapi")
data class SolapiProperties (
    var sender : String = "",
    var apiKey : String = "",
    var apiSecret : String = "",
)