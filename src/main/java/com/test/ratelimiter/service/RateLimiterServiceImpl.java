package com.test.ratelimiter.service;


import com.test.ratelimiter.model.FilterField;
import com.test.ratelimiter.strategies.*;

import java.time.Duration;
import java.util.HashMap;

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
            case TOTAL_COUNT -> this.rateLimiterStrategy = new StrategyTotalCountPeriod<>(slidingWindowDuration, thresholdSize);
        }
    }

    public boolean passRequestByFilterField(FilterField<T> filterField) {
        return rateLimiterStrategy.passRequestByFilterField(filterField);
    }

    public String getStrategyName() {
        return rateLimiterStrategy.toString();
    }

    public HashMap<String, Long> getStatistics(FilterField<T> filterField) {
        if (filterField == null) {
            throw new IllegalArgumentException("filterField cannot be null");
        }
        HashMap<String, Long> statistics = new HashMap<>();
        statistics.put("REQUESTS", rateLimiterStrategy.getStatistics(filterField).getOrDefault("REQUESTS", 0L));
        statistics.put("THRESHOLD", rateLimiterStrategy.getStatistics(filterField).getOrDefault("THRESHOLD", 0L));
        return statistics;
    }


}
