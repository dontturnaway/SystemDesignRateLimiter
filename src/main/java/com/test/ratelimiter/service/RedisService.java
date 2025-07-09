package com.test.ratelimiter.service;

public interface RedisService {
    void save(String key, String value);
    String get(String key);
    Long getTTL(String key);
}