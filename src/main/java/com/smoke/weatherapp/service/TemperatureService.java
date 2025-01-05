package com.smoke.weatherapp.service;

import com.smoke.weatherapp.model.OpenMeteoResponse;
import com.smoke.weatherapp.model.TemperatureData;
import com.smoke.weatherapp.repository.TemperatureDataRepository;
import com.smoke.weatherapp.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class TemperatureService {

    @Value("${open-meteo.api.base-url}")
    private String apiUrl;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private TemperatureDataRepository temperatureDataRepository;

    public TemperatureData getTemperatureData(double latitude, double longitude) {
        if (latitude < Constants.MIN_LATITUDE || latitude > Constants.MAX_LATITUDE ||
                longitude < Constants.MIN_LONGITUDE || longitude > Constants.MAX_LONGITUDE) {
            throw new IllegalArgumentException(Constants.ERROR_INVALID_COORDINATES);
        }

        Optional<TemperatureData> cachedData = temperatureDataRepository.findByLatitudeAndLongitude(latitude, longitude);
        boolean cachedDataIsValid = cachedData.isPresent() && cachedData.get().getTimestamp().isAfter(LocalDateTime.now().minusMinutes(Constants.CACHE_INVALIDATION_TIME_MINUTES));

        if(cachedDataIsValid) {
            return cachedData.get();
        } else {
            return fetchFreshTemperatureData(latitude, longitude);
        }
    }

    private TemperatureData fetchFreshTemperatureData(double latitude, double longitude) {
        String url = apiUrl + "?latitude=" + latitude + "&longitude=" + longitude + "&forecast_days=1";
        OpenMeteoResponse response = restTemplate.getForObject(url, OpenMeteoResponse.class);

        if(response != null && response.getCurrentWeather() != null) {
            TemperatureData temperatureData = new TemperatureData(null, latitude, longitude, response.getCurrentWeather().getTemperature(), LocalDateTime.now());
            temperatureDataRepository.save(temperatureData);
            return temperatureData;
        } else {
            throw new RuntimeException(Constants.ERROR_OPEN_METEO_API_FAILURE);
        }
    }
}
