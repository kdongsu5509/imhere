package com.kdongsu5509.imhere.common.config

import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.springframework.cache.CacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.StringRedisSerializer
import java.time.Duration

@Configuration
class RedisConfig {

    private fun objectMapper(): ObjectMapper {
        val mapper = ObjectMapper()
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        mapper.registerModules(JavaTimeModule(), Jdk8Module())

        mapper.activateDefaultTyping(
            mapper.polymorphicTypeValidator,
            ObjectMapper.DefaultTyping.NON_FINAL,
            JsonTypeInfo.As.PROPERTY
        )
        return mapper
    }

    @Bean
    fun redisTemplate(redisConnectionFactory: RedisConnectionFactory): RedisTemplate<String, Any> {
        val template = RedisTemplate<String, Any>()
        template.connectionFactory = redisConnectionFactory

        // 1. Key Serializer: Key를 읽을 때 문자열(String)로 처리
        template.keySerializer = StringRedisSerializer()

        // 2. Value Serializer: Value를 읽을 때 JSON 형태로 객체에 매핑
        template.valueSerializer = GenericJackson2JsonRedisSerializer(objectMapper())

        // 3. Hash Key/Value Serializer (선택 사항)
        template.hashKeySerializer = StringRedisSerializer()
        template.hashValueSerializer = GenericJackson2JsonRedisSerializer(objectMapper())

        return template
    }

    @Bean
    fun kakaoPublicKeyCacheManager(redisConnectionFactory: RedisConnectionFactory): CacheManager {
        val defaultCacheConfig = RedisCacheConfiguration.defaultCacheConfig()
            .serializeKeysWith(
                RedisSerializationContext.SerializationPair.fromSerializer(StringRedisSerializer())
            )
            .serializeValuesWith( // JSON 형태로 값 직렬화
                RedisSerializationContext.SerializationPair.fromSerializer(
                    GenericJackson2JsonRedisSerializer(objectMapper())
                )
            )
            // 🚨 핵심: 캐시 항목의 유효 기간(TTL)을 7일로 설정합니다.
            .entryTtl(Duration.ofDays(7))
            .disableCachingNullValues() // Null 값은 캐시하지 않음

        // kakaoPublicKeyCacheManager 이름으로 CacheManager 빈을 등록
        return RedisCacheManager.builder(redisConnectionFactory)
            .cacheDefaults(defaultCacheConfig)
            .initialCacheNames(setOf("KakaoPublicKey")) // 캐시 이름을 초기화
            .build()
    }
}