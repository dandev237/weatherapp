package com.smoke.weatherapp.service;

import com.smoke.weatherapp.model.OpenMeteoResponse;
import com.smoke.weatherapp.model.TemperatureData;
import com.smoke.weatherapp.repository.TemperatureDataRepository;
import com.smoke.weatherapp.util.Constants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class TemperatureServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private TemperatureDataRepository temperatureDataRepository;

    @Mock
    private KafkaProducerService kafkaProducerService;

    @InjectMocks
    private TemperatureService temperatureService;

    private final double latitude = 40.7128;
    private final double longitude = -74.0060;
    private final double temperature = 25.5;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        now = LocalDateTime.now();
    }

    private TemperatureData createTemperatureData(LocalDateTime timestamp) {
        return createTemperatureData(timestamp, temperature);
    }

    private TemperatureData createTemperatureData(LocalDateTime timestamp, double temperature) {
        return new TemperatureData(null, latitude, longitude, temperature, timestamp);
    }

    private OpenMeteoResponse createMockResponse(double temperature) {
        OpenMeteoResponse response = new OpenMeteoResponse();
        OpenMeteoResponse.CurrentWeather currentWeather = new OpenMeteoResponse.CurrentWeather();
        currentWeather.setTemperature(temperature);
        response.setCurrentWeather(currentWeather);
        return response;
    }

    @Test
    void testGetTemperatureData_ReturnsCacheIfUpdated() {
        TemperatureData cachedData = createTemperatureData(now);
        when(temperatureDataRepository.findByLatitudeAndLongitude(latitude, longitude))
                .thenReturn(Collections.singletonList(cachedData));

        TemperatureData result = temperatureService.getTemperatureData(latitude, longitude);

        assertNotNull(result);
        assertEquals(temperature, result.getTemperature());
        verify(restTemplate, never()).getForObject(anyString(), eq(OpenMeteoResponse.class));
        verify(kafkaProducerService, times(1)).sendTemperatureDataMessage(cachedData);
    }

    @Test
    void testGetTemperatureData_UsesApiIfCacheNotUpdated() {
        when(temperatureDataRepository.findByLatitudeAndLongitude(latitude, longitude))
                .thenReturn(Collections.emptyList());

        OpenMeteoResponse mockResponse = createMockResponse(temperature);
        when(restTemplate.getForObject(anyString(), eq(OpenMeteoResponse.class)))
                .thenReturn(mockResponse);

        TemperatureData result = temperatureService.getTemperatureData(latitude, longitude);

        assertNotNull(result);
        assertEquals(temperature, result.getTemperature());
        verify(restTemplate, times(1)).getForObject(anyString(), eq(OpenMeteoResponse.class));
        verify(kafkaProducerService, times(1)).sendTemperatureDataMessage(result);
    }

    @Test
    void testFetchFreshTemperatureData_StoresDataIfItIsNew() {
        OpenMeteoResponse mockResponse = createMockResponse(temperature);
        when(restTemplate.getForObject(anyString(), eq(OpenMeteoResponse.class)))
                .thenReturn(mockResponse);

        TemperatureData result = temperatureService.getTemperatureData(latitude, longitude);

        assertNotNull(result);
        assertEquals(temperature, result.getTemperature());
        verify(temperatureDataRepository, times(1)).save(any(TemperatureData.class));
        verify(kafkaProducerService, times(1)).sendTemperatureDataMessage(result);
    }

    @Test
    void testFetchFreshTemperatureData_UpdatesDataIfItExists() {
        TemperatureData existingData = createTemperatureData(now.minusMinutes(10), 24.5);
        when(temperatureDataRepository.findByLatitudeAndLongitude(latitude, longitude))
                .thenReturn(Collections.singletonList(existingData));

        OpenMeteoResponse mockResponse = createMockResponse(temperature);
        when(restTemplate.getForObject(anyString(), eq(OpenMeteoResponse.class)))
                .thenReturn(mockResponse);

        TemperatureData result = temperatureService.getTemperatureData(latitude, longitude);

        assertNotNull(result);
        assertEquals(temperature, result.getTemperature());

        ArgumentCaptor<TemperatureData> captor = ArgumentCaptor.forClass(TemperatureData.class);
        verify(temperatureDataRepository, times(1)).save(captor.capture());
        TemperatureData capturedData = captor.getValue();

        assertEquals(temperature, capturedData.getTemperature());
        assertTrue(capturedData.getTimestamp().isAfter(now.minusMinutes(10)));
        verify(kafkaProducerService, times(1)).sendTemperatureDataMessage(result);
    }

    @Test
    void testFetchFreshTemperatureData_ThrowsExceptionOnApiError() {
        when(restTemplate.getForObject(anyString(), eq(OpenMeteoResponse.class)))
                .thenReturn(null);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            temperatureService.getTemperatureData(latitude, longitude);
        });

        assertEquals(Constants.ERROR_OPEN_METEO_API_FAILURE, exception.getMessage());
    }

    @Test
    void testFetchFreshTemperatureData_ThrowsExceptionOnNullApiResponse() {
        when(restTemplate.getForObject(anyString(), eq(OpenMeteoResponse.class)))
                .thenReturn(new OpenMeteoResponse());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            temperatureService.getTemperatureData(latitude, longitude);
        });

        assertEquals(Constants.ERROR_OPEN_METEO_API_FAILURE, exception.getMessage());
    }

    @Test
    void testGetTemperatureData_ThrowsExceptionOnInvalidCoordinates() {
        double invalidLatitude = 100.0; // Invalid latitude
        double invalidLongitude = 200.0; // Invalid longitude

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            temperatureService.getTemperatureData(invalidLatitude, invalidLongitude);
        });

        assertEquals(Constants.ERROR_INVALID_COORDINATES, exception.getMessage());
    }

    @Test
    void testDeleteTemperatureData_Success() {
        List<TemperatureData> dataList = Collections.singletonList(new TemperatureData(null, latitude, longitude, 25.5, null));
        when(temperatureDataRepository.findByLatitudeAndLongitude(latitude, longitude)).thenReturn(dataList);

        boolean result = temperatureService.deleteTemperatureData(latitude, longitude);

        assertTrue(result);
        verify(temperatureDataRepository, times(1)).deleteAll(dataList);
    }

    @Test
    void testDeleteTemperatureData_NotFound() {
        when(temperatureDataRepository.findByLatitudeAndLongitude(latitude, longitude)).thenReturn(Collections.emptyList());

        boolean result = temperatureService.deleteTemperatureData(latitude, longitude);

        assertFalse(result);
        verify(temperatureDataRepository, never()).deleteAll(anyList());
    }
}