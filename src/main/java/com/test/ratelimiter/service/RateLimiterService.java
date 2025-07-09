package com.test.ratelimiter.service;

import com.test.ratelimiter.model.FilterField;

public interface RateLimiterService {
    boolean passRequest(FilterField filterField);
    String getStrategyInfo();
    String getStatistics(FilterField filterField);
}
