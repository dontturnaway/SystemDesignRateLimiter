package com.test.ratelimiter.service;

import com.test.ratelimiter.model.FilterField;

public interface RateLimiterService<T> {
    boolean passRequest(FilterField<T> filterField);
    String getStrategyInfo();
    String getStatistics(FilterField<T> filterField);
}
