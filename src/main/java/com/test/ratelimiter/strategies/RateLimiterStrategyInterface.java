package com.test.ratelimiter.strategies;

import com.test.ratelimiter.model.FilterField;

import java.util.HashMap;

public interface RateLimiterStrategyInterface<T> {
    boolean passRequestByFilterField(FilterField<T> filterField);
    String getStrategyName();
    HashMap<String, Long> getStatistics(FilterField<T> filterField);
}
