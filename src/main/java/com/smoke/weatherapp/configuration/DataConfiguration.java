package com.smoke.weatherapp.configuration;

import com.smoke.weatherapp.model.TemperatureData;
import com.smoke.weatherapp.repository.TemperatureDataRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

@Configuration
public class DataConfiguration {

    @Bean
    CommandLineRunner initDatabase(TemperatureDataRepository repository) {
        return args -> {
            repository.save(new TemperatureData(
                    null,
                    40.7128,
                    -74.0060,
                    25.5,
                    LocalDateTime.now()
            ));
            repository.findAll().forEach(System.out::println);
        };
    }
}