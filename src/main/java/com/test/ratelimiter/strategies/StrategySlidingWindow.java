package com.test.ratelimiter.strategies;

import com.test.ratelimiter.model.FilterField;
import com.test.ratelimiter.model.FilterFieldIP;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class StrategySlidingWindow implements RateLimiterStrategyInterface {

    /*
    We need 2 data structures: hashmap with <IP, request_count> and Queue with map of <Timestamp, Key>.
    When client calls our service:
     1. We increment the amount in HashMap by 1
     2. We pop the Queue until the value fits in sliding window shows.
     3. As soon as the pop from the Queue is in the progress, we decrement the values from the HashMap
     4. When it's done, we check the final value
     */

    private final HashMap<FilterFieldIP, Integer> requestCounter = new HashMap<>();
    private final Queue<Map<FilterFieldIP, Instant>> requestQueue = new LinkedList<>();
    private final java.time.Duration slidingWindowDuration;

    public StrategySlidingWindow(Duration slidingWindowDuration, Integer thresholdSize) {
        this.slidingWindowDuration = slidingWindowDuration;
    }

    @Override
    public boolean checkThreshold(FilterField filterField) {

        if (!(filterField instanceof FilterFieldIP ipField)) {
            throw new IllegalArgumentException("Expected FilterFieldIP");
        }

        if (slidingWindowDuration == null) {
            throw new IllegalArgumentException("Duration cannot be null");
        }

        synchronized (this) {
            requestCounter.put(ipField, requestCounter.getOrDefault(ipField, 0) + 1);
            requestQueue.add(Map.of(ipField, Instant.now()));
            while (!requestQueue.isEmpty() && fitsSlidingWindow(requestQueue.peek())) {
                FilterFieldIP currentFilterFieldIP = requestQueue.poll().entrySet().iterator().next().getKey();
                requestCounter.put(currentFilterFieldIP, requestCounter.get(currentFilterFieldIP) - 1);
            }
        }

        return false;
    }

    public boolean fitsSlidingWindow(Map<FilterFieldIP, Instant> currentIpDate) {
        Instant currentIpDateExtracted = currentIpDate.entrySet().iterator().next().getValue();
        Duration elapsed = Duration.between(currentIpDateExtracted, Instant.now());
        if (elapsed.compareTo(slidingWindowDuration) < 0) {
            return true;
        }
        return false;
    }

    @Override
    public String getStrategyName() {
        return "Sliding window strategy";
    }



}
