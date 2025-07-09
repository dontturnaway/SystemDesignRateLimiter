package com.test.ratelimiter.strategies;

import org.springframework.stereotype.Component;

public enum RateLimitStrategyType {
    SLIDING_WINDOW,
    TOTAL_COUNT
}
