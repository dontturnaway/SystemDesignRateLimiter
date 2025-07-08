package com.test.ratelimiter;

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
            HttpMethod method
    ) {
        String targetUrl;

        switch (url) {
            case "atlassian":
                targetUrl = "https://www.atlassian.com/software";
                break;
            case "news":
                targetUrl = "https://news.ru/";
                break;
            default:
                return Mono.just("Invalid URL");
        }

        return webClient.method(method)
                .uri(targetUrl)
                //.headers(httpHeaders -> httpHeaders.addAll(headers))
                .headers(httpHeaders -> {
                    headers.forEach((key, values) -> {
                        if (!key.equalsIgnoreCase(HttpHeaders.HOST)) {
                            httpHeaders.addAll(key, values);
                        }
                    });
                })
                .body(body != null ? body : Mono.empty(), String.class)
                .retrieve()
                .bodyToMono(String.class);
    }
}