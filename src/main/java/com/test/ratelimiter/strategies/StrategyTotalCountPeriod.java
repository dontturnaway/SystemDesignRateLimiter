package com.test.ratelimiter.strategies;

import com.test.ratelimiter.model.FilterField;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;


public class StrategyTotalCountPeriod<T> implements RateLimiterStrategyInterface<T> {

    private final HashMap<FilterField<T>, Map<Long, Long>> requestCounter = new HashMap<>();
    private final Duration windowDuration;
    private final Integer windowSize;

    public StrategyTotalCountPeriod(Duration slidingWindowDuration, Integer windowSize) {
        if (slidingWindowDuration == null || windowSize == null) {
            throw new IllegalArgumentException("slidingWindowDuration and thresholdSize cannot be null");
        }
        this.windowDuration = slidingWindowDuration;
        this.windowSize = windowSize;
    }

    @Override
    public boolean passRequestByFilterField(FilterField<T> filterField) {
        synchronized (this) {
            this.updateStatisticsByFilterField(filterField);
            long currentRequestsCount = requestCounter.get(filterField).entrySet().iterator().next().getValue();
            var result =  currentRequestsCount <= windowSize;
            System.out.println("RESULT: " + result + " REQUEST_COUNT: " + currentRequestsCount);
            return result;
        }
    }

    private void updateStatisticsByFilterField(FilterField<T> filterField) {
        Instant now = Instant.now();
        long windowStartSeconds = (now.getEpochSecond() / windowDuration.toSeconds()) * windowDuration.toSeconds();

        if (!requestCounter.containsKey(filterField)) {
            requestCounter.put(filterField, new HashMap<>(Map.of(windowStartSeconds, 1L)));
            return;
        }

        if (requestCounter.get(filterField).containsKey(windowStartSeconds)) {
            requestCounter.get(filterField).put(windowStartSeconds, requestCounter.get(filterField).get(windowStartSeconds) + 1L);
        } else {
            requestCounter.remove(filterField);
            requestCounter.put(filterField, Map.of(windowStartSeconds, 1L));
        }
    }

    @Override
    public String getStrategyName() {
        return "Sliding window strategy";
    }

    @Override
    public HashMap<String, Long> getStatistics(FilterField<T> filterField) {
        HashMap<String, Long> result = new HashMap<>();
        result.put("REQUESTS", requestCounter.get(filterField).entrySet().iterator().next().getValue());
        result.put("THRESHOLD", Long.valueOf(this.windowSize));
        result.put("DURATION", windowDuration.toSeconds());
        return result;
    }


}
