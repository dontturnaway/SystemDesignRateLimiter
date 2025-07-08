package com.test.ratelimiter;

public class RateLimiterSlidingWindow {
    /*
    We need 2 data structures: hashmap with <IP, request_count> and Queue with map of <Timestamp, Key>.
    When client calls our service:
     1. We increment the amount in HashMap by 1
     2. We pop the Queue until the value fits in sliding window shows.
     3. As soon as the pop from the Queue is in the progress, we decrement the values from the HashMap
     4. When it's done, we check the final value


     */
}
