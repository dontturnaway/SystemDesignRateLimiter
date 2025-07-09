package com.test.ratelimiter.strategies;

import com.test.ratelimiter.model.FilterField;

import java.util.HashMap;

public interface RateLimiterStrategyInterface {
    boolean checkThreshold(FilterField filterField);
    String getStrategyName();
    HashMap<String, Integer> getStatistics(FilterField filterField);
}
