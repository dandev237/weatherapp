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
import java.util.List;
import java.util.Optional;

@Service
public class TemperatureService {

    @Value("${open-meteo.api.base-url}")
    private String apiUrl;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private TemperatureDataRepository temperatureDataRepository;

    @Autowired
    private KafkaProducerService kafkaProducerService;

    public TemperatureData getTemperatureData(double latitude, double longitude) {
        if (latitude < Constants.MIN_LATITUDE || latitude > Constants.MAX_LATITUDE ||
                longitude < Constants.MIN_LONGITUDE || longitude > Constants.MAX_LONGITUDE) {
            throw new IllegalArgumentException(Constants.ERROR_INVALID_COORDINATES);
        }

        List<TemperatureData> cachedDataList = temperatureDataRepository.findByLatitudeAndLongitude(latitude, longitude);
        Optional<TemperatureData> cachedData = cachedDataList.stream()
                .filter(data -> data.getTimestamp().isAfter(LocalDateTime.now().minusMinutes(Constants.CACHE_INVALIDATION_TIME_MINUTES)))
                .findFirst();
        boolean cachedDataIsValid = cachedData.isPresent() && cachedData.get().getTimestamp().isAfter(LocalDateTime.now().minusMinutes(Constants.CACHE_INVALIDATION_TIME_MINUTES));

        TemperatureData data;
        if(cachedDataIsValid) {
            data = cachedData.get();
        } else {
            data = fetchFreshTemperatureData(latitude, longitude);
        }

        kafkaProducerService.sendTemperatureDataMessage(data);

        return data;
    }

    private TemperatureData fetchFreshTemperatureData(double latitude, double longitude) {
        String url = apiUrl + "?latitude=" + latitude + "&longitude=" + longitude + "&current_weather=true";
        OpenMeteoResponse response = restTemplate.getForObject(url, OpenMeteoResponse.class);

        if(response != null && response.getCurrentWeather() != null) {
            TemperatureData temperatureData = new TemperatureData(null, latitude, longitude, response.getCurrentWeather().getTemperature(), LocalDateTime.now());
            TemperatureData existingTemperatureData = temperatureDataRepository.findByLatitudeAndLongitude(latitude, longitude).stream().findAny().orElse(null);
            if (existingTemperatureData != null) {
                existingTemperatureData.setTemperature(temperatureData.getTemperature());
                existingTemperatureData.setTimestamp(temperatureData.getTimestamp());
                temperatureDataRepository.save(existingTemperatureData);
                return existingTemperatureData;
            } else {
                temperatureDataRepository.save(temperatureData);
                return temperatureData;
            }
        } else {
            throw new RuntimeException(Constants.ERROR_OPEN_METEO_API_FAILURE);
        }
    }

    public boolean deleteTemperatureData(double latitude, double longitude) {
        List<TemperatureData> dataList = temperatureDataRepository.findByLatitudeAndLongitude(latitude, longitude);
        if (!dataList.isEmpty()) {
            temperatureDataRepository.deleteAll(dataList);
            return true;
        }
        return false;
    }
}