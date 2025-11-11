package com.kdongsu5509.imhere

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableCaching
@EnableScheduling
@EnableAsync
class ImhereApplication

fun main(args: Array<String>) {
    runApplication<ImhereApplication>(*args)
}
