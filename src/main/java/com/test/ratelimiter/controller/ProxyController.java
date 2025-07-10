package com.test.ratelimiter.controller;

import com.test.ratelimiter.model.FilterFieldIP;
import com.test.ratelimiter.service.RateLimiterService;
import com.test.ratelimiter.service.RateLimiterServiceImpl;
import com.test.ratelimiter.strategies.RateLimitStrategyType;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Duration;

@RestController
public class ProxyController {

    private final WebClient webClient = WebClient.create();
    private final RateLimiterService rateLimiterService =
            new RateLimiterServiceImpl(RateLimitStrategyType.SLIDING_WINDOW,
                                        Duration.ofMinutes(1),
                                        10);

    @RequestMapping("/{url}")
    public Mono<String> proxy(
            @PathVariable String url,
            @RequestHeader HttpHeaders headers,
            @RequestBody(required = false) Mono<String> body,
            HttpMethod method,
            HttpServletRequest request
            ) throws UnknownHostException {
        String targetUrl;

        //Emulating call to external services based on L7 URL routing
        switch (url) {
            case "archive":
                targetUrl = "https://web.archive.org/";
                break;
            case "javadoc":
                targetUrl = "https://docs.oracle.com/javase/8/docs/api/java/lang/String.html";
                break;
            default:
                return Mono.error(new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.BAD_REQUEST, "URL path must be archive or javadoc"
                ));
        }

        //Calling rate limiter service to abort processing, providing IP to it
        FilterFieldIP ipToFilter = new FilterFieldIP(request.getRemoteAddr());
        if (!rateLimiterService.passRequest(ipToFilter)) {
            return Mono.just("Rate limit exceeded")
                    .flatMap(msg -> Mono.error(new org.springframework.web.server.ResponseStatusException(
                            org.springframework.http.HttpStatus.TOO_MANY_REQUESTS, rateLimiterService.getStatistics(ipToFilter)
                    )));
        }

        return webClient.method(method)
                .uri(targetUrl)
                //.headers(httpHeaders -> httpHeaders.addAll(headers))
                .headers(httpHeaders -> {
                    headers.forEach((key, values) -> {
                        if (!HttpHeaders.HOST.equalsIgnoreCase(key)) {
                            httpHeaders.addAll(key, values);
                        }
                    });
                }) //remove HOST header to avoid using wrong port and add all other headers
                .body(body != null ? body : Mono.empty(), String.class)
                .retrieve()
                .bodyToMono(String.class);
    }
}