package com.test.ratelimiter;

import com.test.ratelimiter.service.RedisService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class RateLimiterApplication {

	public static void main(String[] args) {
		SpringApplication.run(RateLimiterApplication.class, args);
	}

	@Bean
	CommandLineRunner init(RedisService redisService) {
		System.out.println("Bootstrapping Redis with some values...");

		return args -> {};
	}
}