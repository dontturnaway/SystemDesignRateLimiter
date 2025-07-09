package com.test.ratelimiter.strategies;

import com.test.ratelimiter.model.FilterField;

import java.util.HashMap;

public class StrategyTotalCount implements RateLimiterStrategyInterface {


    @Override
    public boolean passRequest(FilterField filterField) {
        return false;
    }

    @Override
    public String getStrategyName() {
        return "";
    }

    @Override
    public HashMap<String, Integer> getStatistics(FilterField filterField) {
        return null;
    }
}
