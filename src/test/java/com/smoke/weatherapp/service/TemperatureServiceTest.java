package com.smoke.weatherapp.service;

import com.smoke.weatherapp.model.OpenMeteoResponse;
import com.smoke.weatherapp.model.TemperatureData;
import com.smoke.weatherapp.repository.TemperatureDataRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class TemperatureServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private TemperatureDataRepository temperatureDataRepository;

    @InjectMocks
    private TemperatureService temperatureService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetTemperatureData_UsesApiIfCacheNotUpdated() {
        double latitude = 40.7128;
        double longitude = -74.0060;

        when(temperatureDataRepository.findByLatitudeAndLongitude(latitude, longitude))
                .thenReturn(Optional.empty());

        OpenMeteoResponse mockResponse = new OpenMeteoResponse();
        OpenMeteoResponse.CurrentWeather currentWeather = new OpenMeteoResponse.CurrentWeather();
        currentWeather.setTemperature(25.5);
        mockResponse.setCurrentWeather(currentWeather);

        when(restTemplate.getForObject(anyString(), eq(OpenMeteoResponse.class)))
                .thenReturn(mockResponse);

        TemperatureData result = temperatureService.getTemperatureData(latitude, longitude);

        assertNotNull(result);
        assertEquals(25.5, result.getTemperature());
        verify(restTemplate, times(1)).getForObject(anyString(), eq(OpenMeteoResponse.class));
    }

    @Test
    void testFetchFreshTemperatureData_StoresDataInMongoDB() {
        double latitude = 40.7128;
        double longitude = -74.0060;

        OpenMeteoResponse mockResponse = new OpenMeteoResponse();
        OpenMeteoResponse.CurrentWeather currentWeather = new OpenMeteoResponse.CurrentWeather();
        currentWeather.setTemperature(25.5);
        mockResponse.setCurrentWeather(currentWeather);

        when(restTemplate.getForObject(anyString(), eq(OpenMeteoResponse.class)))
                .thenReturn(mockResponse);

        TemperatureData result = temperatureService.getTemperatureData(latitude, longitude);

        assertNotNull(result);
        assertEquals(25.5, result.getTemperature());
        verify(temperatureDataRepository, times(1)).save(any(TemperatureData.class));
    }
}