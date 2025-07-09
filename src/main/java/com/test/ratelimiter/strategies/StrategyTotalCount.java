package com.test.ratelimiter.strategies;

public class StrategyTotalCount implements RateLimiterStrategyInterface {
    @Override
    public boolean checkTrashhold() {
        return true;
    }

    @Override
    public String getStrategyName() {
        return "Sliding total count";
    }


}
