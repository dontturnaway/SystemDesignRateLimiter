package com.test.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisDashboardService {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;


    public RedisDashboardService(StringRedisTemplate redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    public void saveDashboard(String key, Dashboard dashboard) {
        try {
            String json = objectMapper.writeValueAsString(dashboard);
            redisTemplate.opsForValue().set(key, json);
        } catch (Exception e) {
            throw new RuntimeException("Could not serialize Dashboard", e);
        }
    }

    public Dashboard getDashboard(String key) {
        try {
            String json = redisTemplate.opsForValue().get(key);
            if (json == null) return null;
            return objectMapper.readValue(json, Dashboard.class);
        } catch (Exception e) {
            throw new RuntimeException("Could not deserialize Dashboard", e);
        }
    }
}