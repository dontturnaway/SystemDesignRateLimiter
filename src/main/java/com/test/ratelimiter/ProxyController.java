package com.test.ratelimiter;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestController
public class ProxyController {

    private final WebClient webClient = WebClient.create();

    @RequestMapping("/{url}/**")
    public Mono<String> proxy(ServerWebExchange exchange, @PathVariable String url) {
        String backendUrl;

        switch (url) {
            case "redis":
                backendUrl = "http://localhost:8080";
                break;
            case "rate-limiter":
                backendUrl = "http://localhost:8081";
                break;
            default:
                return Mono.just("Invalid URL");
        }

        String path = exchange.getRequest().getPath().subPath(1).value(); // full path after /
        String targetUri = backendUrl + "/" + path;

        return webClient.method(exchange.getRequest().getMethod())
                .uri(targetUri)
                .headers(httpHeaders -> {
                    exchange.getRequest().getHeaders().forEach(httpHeaders::addAll);
                })
                .contentType(MediaType.APPLICATION_JSON)
                .body(exchange.getRequest().getBody(), String.class)
                .retrieve()
                .bodyToMono(String.class);
    }
}