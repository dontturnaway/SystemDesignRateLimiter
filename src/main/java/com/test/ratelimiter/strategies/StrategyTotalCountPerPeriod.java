package com.test.ratelimiter.strategies;

import com.test.ratelimiter.model.FilterField;

import java.util.HashMap;

public class StrategyTotalCountPerPeriod<T> implements RateLimiterStrategyInterface<T> {


    @Override
    public boolean passRequest(FilterField<T> filterField) {
        return false;
    }

    @Override
    public String getStrategyName() {
        return "StrategyTotalCount";
    }

    @Override
    public HashMap<String, Integer> getStatistics(FilterField<T> filterField) {
        return null;
    }
}
