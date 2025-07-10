package com.test.ratelimiter.strategies;

import com.test.ratelimiter.model.FilterField;

import java.util.HashMap;

public interface RateLimiterStrategyInterface<T> {
    boolean passRequest(FilterField<T> filterField);
    String getStrategyName();
    HashMap<String, Integer> getStatistics(FilterField<T> filterField);
}
