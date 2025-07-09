package com.test.ratelimiter.service;

import com.test.ratelimiter.model.FilterField;

public interface RateLimiterInterface {
    boolean tryAcquire(FilterField filterField);
    String getStrategyInfo();
}
