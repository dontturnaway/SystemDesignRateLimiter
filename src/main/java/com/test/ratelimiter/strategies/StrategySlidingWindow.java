package com.test.ratelimiter.strategies;

import com.test.ratelimiter.model.FilterField;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class StrategySlidingWindow<T> implements RateLimiterStrategyInterface<T> {

    /*
    We need 2 data structures: hashmap with <IP, request_count> and Queue with map of <Timestamp, Key>.
    When client calls our service:
     1. We increment the amount in HashMap by 1
     2. We pop the Queue until the value fits in sliding window shows.
     3. As soon as the pop from the Queue is in the progress, we decrement the values from the HashMap
     4. When it's done, we check the final value
     */

    private final HashMap<FilterField<T>, Integer> requestCounter = new HashMap<>();
    private final Queue<Map<FilterField<T>, Instant>> requestQueue = new LinkedList<>();
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
    public boolean passRequest(FilterField<T> filterField) {


        synchronized (this) {
            this.updateStatisticsByFilterField(filterField);
            var result =  requestCounter.get(filterField) <= thresholdSize;
            System.out.println("RESULT: " + result + " REQUEST_COUNT: " + requestCounter.get(filterField));
            return result;
        }
    }

    private void updateStatisticsByFilterField(FilterField<T> filterField) {
        synchronized (this) {
            while (!requestQueue.isEmpty() && (!fitsSlidingWindow(requestQueue.peek()))) {
                FilterField<T> staleIp = requestQueue.poll().entrySet().iterator().next().getKey();
                requestCounter.put(staleIp, requestCounter.get(staleIp) - 1);
            }
            requestCounter.put(filterField, requestCounter.getOrDefault(filterField, 0) + 1);
            requestQueue.add(Map.of(filterField, Instant.now()));
        }
    }

    public boolean fitsSlidingWindow(Map<FilterField<T>, Instant> currentIpDate) {
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
    public HashMap<String, Integer> getStatistics(FilterField<T> filterField) {
        if (!(filterField instanceof FilterField<T> ipField)) {
            throw new IllegalArgumentException("Expected FilterFieldIP");
        }
        HashMap<String, Integer> result = new HashMap<>();
        result.put("REQUESTS", requestCounter.getOrDefault(ipField,0));
        result.put("THRESHOLD", this.thresholdSize);
        return result;
    }


}
