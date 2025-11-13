package com.kdongsu5509.imhere.auth.adapter.out.redis

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.GenericContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName
import java.time.Duration
import java.util.concurrent.TimeUnit

@Testcontainers
@SpringBootTest
class RedisCacheAdapterTest {

    @Autowired
    private lateinit var redisCacheAdapter: RedisCacheAdapter

    companion object {
        @Container
        @JvmStatic
        val redisContainer: GenericContainer<*> =
            GenericContainer(DockerImageName.parse("redis:6-alpine"))
                .withExposedPorts(6379)

        @JvmStatic
        @DynamicPropertySource
        fun setRedisProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.data.redis.host") { redisContainer.host }
            registry.add("spring.data.redis.port") { redisContainer.getMappedPort(6379).toString() }
        }
    }

    @Test
    @DisplayName("데이터를 성공적으로 저장하고 조회한다")
    fun save_and_find_success() {
        val testKey = "testKey:testKey"
        val testValue = "testValue"
        val duration = Duration.ofSeconds(60)

        redisCacheAdapter.save(testKey, testValue, duration)

        val foundValue = redisCacheAdapter.find(testKey)

        assertThat(foundValue).isEqualTo(testValue)
    }

    @Test
    @DisplayName("만료 시간이 지나면 데이터를 찾을 수 없다")
    fun expire_time_works_correctly() {
        val testKey = "testKey:expire"
        val testValue = 100
        val shortDuration = Duration.ofSeconds(1)

        redisCacheAdapter.save(testKey, testValue, shortDuration)

        TimeUnit.SECONDS.sleep(2)

        val foundValue = redisCacheAdapter.find(testKey)

        assertThat(foundValue).isNull()
    }
}