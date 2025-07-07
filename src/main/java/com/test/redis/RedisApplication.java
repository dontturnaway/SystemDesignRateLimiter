package com.test.redis;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication
public class RedisApplication {

	public static void main(String[] args) {
		SpringApplication.run(RedisApplication.class, args);
	}

	@Bean
	CommandLineRunner init(RedisService redisService, RedisDashboardService redisDashboardService) {
		System.out.println("Bootstrapping Redis with some values...");

		return args -> {};
	}
}