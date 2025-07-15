package com.test.ratelimiter.controller;

import com.test.ratelimiter.model.FilterFieldIP;
import com.test.ratelimiter.service.RateLimiterService;
import com.test.ratelimiter.service.RateLimiterServiceImpl;
import com.test.ratelimiter.strategies.RateLimitStrategyType;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Map;

@RestController
public class ProxyController {

    private final WebClient webClient = WebClient.create();
    private final RateLimiterService<byte[]> rateLimiterService =
            new RateLimiterServiceImpl<>(RateLimitStrategyType.TOTAL_COUNT,
                    Duration.ofMinutes(1),
                    5);

    @RequestMapping("/{url}")
    public Mono<ResponseEntity<String>> proxy(
            @PathVariable String url,
            @RequestHeader HttpHeaders headers,
            @RequestBody(required = false) Mono<String> body,
            HttpMethod method,
            HttpServletRequest request
    ) {
        String targetUrl;

        switch (url) {
            case "archive":
                targetUrl = "https://web.archive.org/";
                break;
            case "javadoc":
                targetUrl = "https://docs.oracle.com/javase/8/docs/api/java/lang/String.html";
                break;
            default:
                return Mono.just(ResponseEntity
                        .badRequest()
                        .body("URL path must be archive or javadoc"));
        }

        FilterFieldIP ipToFilter = new FilterFieldIP(request.getRemoteAddr());

        if (!rateLimiterService.passRequestByFilterField(ipToFilter)) {
            HttpHeaders responseHeaders = addRateLimitHeaders(new HttpHeaders(), ipToFilter);
            return Mono.just(ResponseEntity
                    .status(HttpStatus.TOO_MANY_REQUESTS)
                    .headers(responseHeaders)
                    .body("Rate limit exceeded"));
        }

        return webClient.method(method)
                .uri(targetUrl)
                .headers(httpHeaders -> headers.forEach((key, values) -> {
                    if (!HttpHeaders.HOST.equalsIgnoreCase(key)) {
                        httpHeaders.addAll(key, values);
                    }
                }))
                .body(body != null ? body : Mono.empty(), String.class)
                .retrieve()
                .toEntity(String.class)
                .map(responseEntity -> ResponseEntity.status(responseEntity.getStatusCode())
                        .headers(addRateLimitHeaders(responseEntity.getHeaders(), ipToFilter))
                        .body(responseEntity.getBody())
                );
    }

    private HttpHeaders addRateLimitHeaders(HttpHeaders original, FilterFieldIP ipToFilter) {
        Map<String, Long> stats = rateLimiterService.getStatistics(ipToFilter);
        HttpHeaders headers = new HttpHeaders();
        headers.putAll(original);
        headers.add("X-RateLimit-CurrentRequests", String.valueOf(stats.getOrDefault("REQUESTS", 0L)));
        headers.add("X-RateLimit-RequestsLimit", String.valueOf(stats.getOrDefault("THRESHOLD", 0L)));
        headers.add("X-RateLimit-DurationWindow", String.valueOf(stats.getOrDefault("DURATION", 0L)));
        return headers;
    }


}