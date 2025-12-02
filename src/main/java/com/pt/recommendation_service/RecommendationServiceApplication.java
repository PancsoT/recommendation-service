package com.pt.recommendation_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the Recommendation Service Spring Boot application.
 * <p>
 * This class bootstraps the application using Spring Boot's auto-configuration and component scanning.
 * </p>
 */
@SpringBootApplication
public class RecommendationServiceApplication {

	/**
	 * Starts the Recommendation Service application.
	 *
	 * @param args command-line arguments passed to the application
	 */
	public static void main(String[] args) {
		SpringApplication.run(RecommendationServiceApplication.class, args);
	}

}