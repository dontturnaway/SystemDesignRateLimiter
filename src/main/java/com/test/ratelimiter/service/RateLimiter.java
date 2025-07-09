package com.test.ratelimiter.service;

import com.test.ratelimiter.model.FilterField;

public interface RateLimiter {
    boolean tryAcquire(FilterField filterField, java.time.Duration duration);
    String getStrategyInfo();
}
