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
    private final Integer thresholdSize;

    public StrategySlidingWindow(Duration slidingWindowDuration, Integer thresholdSize) {
        if (slidingWindowDuration == null || thresholdSize == null) {
            throw new IllegalArgumentException("slidingWindowDuration and thresholdSize cannot be null");
        }
        this.slidingWindowDuration = slidingWindowDuration;
        this.thresholdSize = thresholdSize;
    }

    @Override
    public boolean passRequest(FilterField filterField) {

        if (!(filterField instanceof FilterFieldIP ipField)) {
            throw new IllegalArgumentException("Expected FilterFieldIP");
        }

        synchronized (this) {
            this.updateStatisticsByIp(ipField);
            return requestCounter.get(ipField) < thresholdSize;
        }
    }

    private void updateStatisticsByIp(FilterFieldIP ipField) {
        synchronized (this) {
            while (!requestQueue.isEmpty() && fitsSlidingWindow(requestQueue.peek())) {
                FilterFieldIP currentFilterFieldIP = requestQueue.poll().entrySet().iterator().next().getKey();
                requestCounter.put(currentFilterFieldIP, requestCounter.get(currentFilterFieldIP) - 1);
            }
            requestCounter.put(ipField, requestCounter.getOrDefault(ipField, 0) + 1);
            requestQueue.add(Map.of(ipField, Instant.now()));
        }
    }

    public boolean fitsSlidingWindow(Map<FilterFieldIP, Instant> currentIpDate) {
        Instant currentIpDateExtracted = currentIpDate.entrySet().iterator().next().getValue();
        Duration elapsed = Duration.between(currentIpDateExtracted, Instant.now());
        if (slidingWindowDuration.compareTo(elapsed) > 0) {
            return true;
        }
        return false;
    }

    @Override
    public String getStrategyName() {
        return "Sliding window strategy";
    }

    @Override
    public HashMap<String, Integer> getStatistics(FilterField filterField) {
        if (!(filterField instanceof FilterFieldIP ipField)) {
            throw new IllegalArgumentException("Expected FilterFieldIP");
        }
        HashMap<String, Integer> result = new HashMap<>();
        result.put("REQUESTS", requestCounter.getOrDefault(ipField,0));
        result.put("THRESHOLD", this.thresholdSize);
        return result;
    }


}
