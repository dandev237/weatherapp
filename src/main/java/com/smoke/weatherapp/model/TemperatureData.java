package com.smoke.weatherapp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Document(collection = "temperature_data")
public class TemperatureData {

    @Id
    private String id;

    private double latitude;

    private double longitude;

    private double temperature;

    private LocalDateTime timestamp;
}