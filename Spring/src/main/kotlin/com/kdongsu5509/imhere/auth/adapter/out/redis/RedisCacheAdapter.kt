package com.kdongsu5509.imhere.auth.adapter.out.redis

import com.kdongsu5509.imhere.auth.application.port.out.CachePort
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.time.Duration

@Service
class RedisCacheAdapter(private val redisTemplate: RedisTemplate<String, Any>) : CachePort {
    override fun save(key: String, data: Any, duration: Duration) {
        val ops = redisTemplate.opsForValue()
        ops.set(key, data, duration)
    }

    override fun find(key: String): Any? {
        val ops = redisTemplate.opsForValue()
        return ops.get(key)
    }
}