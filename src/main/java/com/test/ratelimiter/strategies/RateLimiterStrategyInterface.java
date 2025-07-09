package com.test.ratelimiter.strategies;

import com.test.ratelimiter.model.FilterField;

public interface RateLimiterStrategyInterface {
    boolean checkThreshold(FilterField filterField);
    String getStrategyName();
}
