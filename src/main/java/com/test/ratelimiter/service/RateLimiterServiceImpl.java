package com.test.ratelimiter.service;


import com.test.ratelimiter.model.FilterField;
import com.test.ratelimiter.strategies.RateLimitStrategyType;
import com.test.ratelimiter.strategies.RateLimiterStrategyInterface;
import com.test.ratelimiter.strategies.StrategySlidingWindow;
import com.test.ratelimiter.strategies.StrategyTotalCount;
import org.springframework.stereotype.Service;

import java.time.Duration;

public class RateLimiterServiceImpl implements RateLimiterService {
    private RateLimiterStrategyInterface rateLimiterStrategy;
    private Duration slidingWindowDuration;
    private Integer thresholdSize;

    public RateLimiterServiceImpl(RateLimitStrategyType rateLimitStrategyType,
                                  Duration slidingWindowDuration,
                                  Integer thresholdSize) {
        switch (rateLimitStrategyType) {
            case SLIDING_WINDOW -> this.rateLimiterStrategy = new StrategySlidingWindow(slidingWindowDuration, thresholdSize);
            case TOTAL_COUNT -> this.rateLimiterStrategy = new StrategyTotalCount();
        }
        this.slidingWindowDuration = slidingWindowDuration;
        this.thresholdSize = thresholdSize;
    }

    public boolean tryAcquire(FilterField filterField) {
        return rateLimiterStrategy.checkThreshold(filterField);
    }

    public String getStrategyInfo() {
        return rateLimiterStrategy.toString();
    }

    public String getStatistics(FilterField filterField) {
        return "Your IP hits :"
                + rateLimiterStrategy.getStatistics(filterField).get("Sliding Window Threshold").toString()
                + "Current threshold: "
                + rateLimiterStrategy.getStatistics(filterField).get("Sliding Window Requests");
    }


}
