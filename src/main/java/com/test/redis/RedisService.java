package com.test.redis;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Service
public class RedisService {

    private final StringRedisTemplate redisTemplate;
    private final Duration cacheTTL = Duration.ofMinutes(100);

    public RedisService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void save(String key, String value) {
        redisTemplate.opsForValue().set(key, value, cacheTTL);
    }

    public String get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public Long getTTL(String key) {
        return redisTemplate.getExpire(key, TimeUnit.MINUTES);
    }
}