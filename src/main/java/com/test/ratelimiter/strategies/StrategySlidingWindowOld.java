package com.test.ratelimiter.strategies;

import com.test.ratelimiter.model.FilterField;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;


//Also could be done in more optimal way with HashMap<Key, Queue<Timestamp>> or using ZSET in Redis
//Redone in a more optimal way in StrategySlidingWindow
@Deprecated
public class StrategySlidingWindowOld<T> implements RateLimiterStrategyInterface<T> {

    private final HashMap<FilterField<T>, Integer> requestCounter = new HashMap<>();
    private final Queue<Map<FilterField<T>, Instant>> requestQueue = new LinkedList<>();
    private final java.time.Duration slidingWindowDuration;
    private final Integer maxRequestsThreshold;

    public StrategySlidingWindowOld(Duration slidingWindowDuration, Integer maxRequestsThreshold) {
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
            var result =  requestCounter.get(filterField) <= maxRequestsThreshold;
            System.out.println("RESULT: " + result + " REQUEST_COUNT: " + requestCounter.get(filterField));
            return result;
        }
    }

    private void updateStatisticsByFilterField(FilterField<T> filterField) {
            while (!requestQueue.isEmpty() && (!fitsSlidingWindow(requestQueue.peek()))) {
                FilterField<T> staleIp = requestQueue.poll().entrySet().iterator().next().getKey();
                requestCounter.put(staleIp, requestCounter.get(staleIp) - 1);
            }
            requestCounter.put(filterField, requestCounter.getOrDefault(filterField, 0) + 1);
            requestQueue.add(Map.of(filterField, Instant.now()));
    }

    public boolean fitsSlidingWindow(Map<FilterField<T>, Instant> currentIpDate) {
        Instant currentIpDateExtracted = currentIpDate.entrySet().iterator().next().getValue();
        Duration elapsed = Duration.between(currentIpDateExtracted, Instant.now());
        return slidingWindowDuration.compareTo(elapsed) > 0;
    }


    @Override
    public String getStrategyName() {
        return "Sliding window strategy";
    }

    @Override
    public HashMap<String, Long> getStatistics(FilterField<T> filterField) {
        HashMap<String, Long> result = new HashMap<>();
        result.put("REQUESTS", requestCounter.getOrDefault(filterField, 0).longValue());
        result.put("THRESHOLD", this.maxRequestsThreshold.longValue());
        result.put("DURATION", slidingWindowDuration.toSeconds());
        return result;
    }


}
