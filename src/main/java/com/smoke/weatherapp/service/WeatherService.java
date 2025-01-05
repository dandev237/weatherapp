package com.smoke.weatherapp.service;

import com.smoke.weatherapp.model.OpenMeteoResponse;
import com.smoke.weatherapp.model.TemperatureData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;

@Service
public class WeatherService {

    @Value("${open-meteo.api.base-url}")
    private String apiUrl;

    @Autowired
    private RestTemplate restTemplate;

    public TemperatureData getTemperatureData(double latitude, double longitude) {
        String url = apiUrl + "?latitude=" + latitude + "&longitude=" + longitude + "&forecast_days=1";
        OpenMeteoResponse response = restTemplate.getForObject(url, OpenMeteoResponse.class);

        if(response != null && response.getCurrentWeather() != null) {
            return new TemperatureData(null, latitude, longitude, response.getCurrentWeather().getTemperature(), LocalDateTime.now());
        } else {
            throw new RuntimeException("Failed to get temperature data from Open-Meteo API");
        }
    }
}
