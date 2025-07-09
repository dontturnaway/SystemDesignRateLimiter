package com.test.ratelimiter.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RestController
public class ProxyController {

    private final WebClient webClient = WebClient.create();

    @RequestMapping("/{url}")
    public Mono<String> proxy(
            @PathVariable String url,
            @RequestHeader HttpHeaders headers,
            @RequestBody(required = false) Mono<String> body,
            HttpMethod method) {
        String targetUrl;

        switch (url) {
            case "archive":
                targetUrl = "https://web.archive.org/";
                break;
            case "javadoc":
                targetUrl = "https://docs.oracle.com/javase/8/docs/api/java/lang/String.html";
                break;
            default:
                return Mono.just("Invalid URL");
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