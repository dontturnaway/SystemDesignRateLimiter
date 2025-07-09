package com.test.ratelimiter.service;


import com.test.ratelimiter.model.FilterField;
import com.test.ratelimiter.strategies.RateLimitStrategyType;
import com.test.ratelimiter.strategies.RateLimiterStrategyInterface;
import com.test.ratelimiter.strategies.StrategySlidingWindow;
import com.test.ratelimiter.strategies.StrategyTotalCount;

public abstract class RateLimiterService implements RateLimiterStrategyInterface {
    private RateLimiterStrategyInterface rateLimiterStrategy;

    public RateLimiterService(RateLimitStrategyType rateLimitStrategyType) {
        switch (rateLimitStrategyType) {
            case SLIDING_WINDOW -> this.rateLimiterStrategy = new StrategySlidingWindow();
            case TOTAL_COUNT -> this.rateLimiterStrategy = new StrategyTotalCount();
        }
    }

    public boolean tryAcquire(FilterField filterField) {
        return rateLimiterStrategy.checkThreshold(filterField);
    }

    public String getStrategyInfo() {
        return rateLimiterStrategy.toString();
    }


}
