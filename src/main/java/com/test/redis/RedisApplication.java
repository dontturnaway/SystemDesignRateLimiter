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

		System.out.println();
		System.out.println("===Working with strings===");
		redisService.save("user:1", "John Smith");
		redisService.save("user:2", "Jack Daniels");

		System.out.println("Getting keys:");
		String user1Name =  redisService.get("key1");
		String user2Name =  redisService.get("key2");

		System.out.println("User1 name is " + user1Name);
		System.out.println("User2 name is " + user2Name);

		System.out.println();
		System.out.println("===Working with JSON===");

		Dashboard dash = new Dashboard(49, List.of(1, 2, 3));
		redisDashboardService.saveDashboard("dashboard", dash);

		Dashboard loaded = redisDashboardService.getDashboard("dashboard");
		System.out.println("Loaded dashboard: " + loaded.getTotal() + ", cards: " + loaded.getCards());

		return args -> {};
	}
}