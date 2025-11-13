package com.kdongsu5509.imhere.auth.adapter.out.redis

import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.testcontainers.containers.GenericContainer
import org.testcontainers.utility.DockerImageName

@TestConfiguration
class TestRedisConfiguration {

    private val redisContainer: GenericContainer<*> =
        GenericContainer(DockerImageName.parse("redis:7.4.1-alpine3.20"))
            .withExposedPorts(6379)

    @PostConstruct
    fun startContainer() {
        redisContainer.start()
    }

    @PreDestroy
    fun stopContainer() {
        redisContainer.stop()
    }

    @Bean
    fun redisConnectionFactory(): RedisConnectionFactory {
        return LettuceConnectionFactory(
            redisContainer.host,
            redisContainer.getMappedPort(6379)
        )
    }
}