package com.test.ratelimiter.service;


import com.test.ratelimiter.model.FilterField;
import com.test.ratelimiter.strategies.RateLimitStrategyType;
import com.test.ratelimiter.strategies.RateLimiterStrategyInterface;
import com.test.ratelimiter.strategies.StrategySlidingWindow;
import com.test.ratelimiter.strategies.StrategyTotalCountPerPeriod;

import java.time.Duration;

public class RateLimiterServiceImpl<T> implements RateLimiterService<T> {
    private RateLimiterStrategyInterface<T> rateLimiterStrategy;


    public RateLimiterServiceImpl(RateLimitStrategyType rateLimitStrategyType,
                                  Duration slidingWindowDuration,
                                  Integer thresholdSize) {
        if (slidingWindowDuration == null || thresholdSize == null) {
            throw new IllegalArgumentException("slidingWindowDuration and thresholdSize cannot be null");
        }
        switch (rateLimitStrategyType) {
            case SLIDING_WINDOW -> this.rateLimiterStrategy = new StrategySlidingWindow<>(slidingWindowDuration, thresholdSize);
            case TOTAL_COUNT -> this.rateLimiterStrategy = new StrategyTotalCountPerPeriod<>();
        }
    }

    public boolean passRequest(FilterField<T> filterField) {
        return rateLimiterStrategy.passRequest(filterField);
    }

    public String getStrategyInfo() {
        return rateLimiterStrategy.toString();
    }

    public String getStatistics(FilterField<T> filterField) {
        if (filterField == null) {
            throw new IllegalArgumentException("filterField cannot be null");
        }
        return "Your IP hits :"
                + rateLimiterStrategy.getStatistics(filterField).getOrDefault("REQUESTS",0).toString()
                + " Current threshold: "
                + rateLimiterStrategy.getStatistics(filterField).getOrDefault("THRESHOLD",0).toString();
    }


}
