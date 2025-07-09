package com.test.ratelimiter.service;

import com.test.ratelimiter.model.FilterField;

import java.util.HashMap;

public interface RateLimiterService {
    boolean tryAcquire(FilterField filterField);
    String getStrategyInfo();
    String getStatistics(FilterField filterField);
}
