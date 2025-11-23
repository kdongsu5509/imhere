package com.kdongsu5509.imhere

import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.GenericContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName

@Testcontainers
abstract class TestRedisContainer {

    companion object {
        private const val REDIS_PORT = 6379

        @Container
        val redisContainer = GenericContainer(DockerImageName.parse("redis:alpine"))
            .withExposedPorts(REDIS_PORT)

        @JvmStatic
        @DynamicPropertySource
        fun configureProperties(registry: DynamicPropertyRegistry) {
            // CI 환경 호환성을 위해 Redis 연결 정보는 코드로 주입
            registry.add("spring.data.redis.host") { redisContainer.host }
            registry.add("spring.data.redis.port") { redisContainer.getMappedPort(REDIS_PORT) }
        }
    }
    
}