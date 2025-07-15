package com.test.ratelimiter.service;

import com.test.ratelimiter.model.FilterField;

public interface RateLimiterService<T> {
    boolean passRequestByFilterField(FilterField<T> filterField);
    String getStrategyName();
    String getStatistics(FilterField<T> filterField);
}
