package com.test.ratelimiter.service;

import com.test.ratelimiter.model.FilterField;

import java.util.HashMap;

public interface RateLimiterService<T> {
    boolean passRequestByFilterField(FilterField<T> filterField);
    String getStrategyName();
    HashMap<String, Long> getStatistics(FilterField<T> filterField);
}
