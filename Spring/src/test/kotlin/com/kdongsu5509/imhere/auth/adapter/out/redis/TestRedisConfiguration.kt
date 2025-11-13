package com.kdongsu5509.imhere.auth.adapter.out.redis

// [설정 파일] TestRedisConfiguration.kt (Kotlin Style)

import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.testcontainers.containers.GenericContainer
import org.testcontainers.utility.DockerImageName

@TestConfiguration // 또는 @Configuration
class TestRedisConfiguration {

    // 컨테이너 인스턴스를 클래스 레벨에서 선언
    private val redisContainer: GenericContainer<*> =
        GenericContainer(DockerImageName.parse("redis:7.4.1-alpine3.20"))
            .withExposedPorts(6379)

    @PostConstruct // Spring Bean 초기화 시 컨테이너 시작
    fun startContainer() {
        redisContainer.start()
    }

    @PreDestroy // Spring Context 종료 시 컨테이너 중지
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