package com.smoke.weatherapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.validation.annotation.Validated;

@SpringBootApplication
@EnableMongoRepositories(basePackages = "com.smoke.weatherapp.repository")
@Validated
public class WeatherAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(WeatherAppApplication.class, args);
	}

}
