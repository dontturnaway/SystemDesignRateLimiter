package com.test.ratelimiter.strategies;

import com.test.ratelimiter.model.FilterField;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;


//Could also be done in Redis using ZSET in Redis in case of multicurrency
public class StrategySlidingWindow<T> implements RateLimiterStrategyInterface<T> {

    private final HashMap<FilterField<T>, Queue<Instant>> requestCounter = new HashMap<>();
    private final java.time.Duration slidingWindowDuration;
    private final Integer maxRequestsThreshold;

    public StrategySlidingWindow(Duration slidingWindowDuration, Integer maxRequestsThreshold) {
        if (slidingWindowDuration == null || maxRequestsThreshold == null) {
            throw new IllegalArgumentException("slidingWindowDuration and thresholdSize cannot be null");
        }
        this.slidingWindowDuration = slidingWindowDuration;
        this.maxRequestsThreshold = maxRequestsThreshold;
    }

    @Override
    public boolean passRequestByFilterField(FilterField<T> filterField) {
        synchronized (this) {
            this.updateStatisticsByFilterField(filterField);
            var result =  requestCounter.get(filterField).size() <= maxRequestsThreshold;
            System.out.println("RESULT: " + result + " REQUEST_COUNT: " + requestCounter.get(filterField));
            return result;
        }
    }

    private void updateStatisticsByFilterField(FilterField<T> filterField) {
        if (!requestCounter.containsKey(filterField)) {
            requestCounter.put(filterField, new LinkedList<>());
        }

        var currentIpQueue = requestCounter.get(filterField);
        currentIpQueue.add(Instant.now());
        while (currentIpQueue.peek() != null &&
                Duration.between(currentIpQueue.peek(), Instant.now()).compareTo(slidingWindowDuration) > 0) {
            currentIpQueue.poll();
        }
    }

    @Override
    public String getStrategyName() {
        return "Sliding window strategy";
    }

    @Override
    public HashMap<String, Integer> getStatistics(FilterField<T> filterField) {
        HashMap<String, Integer> result = new HashMap<>();
        result.put("REQUESTS", requestCounter.getOrDefault(filterField, new LinkedList<>()).size());
        result.put("THRESHOLD", this.maxRequestsThreshold);
        return result;
    }


}
