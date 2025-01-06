package com.smoke.weatherapp.service;

import com.smoke.weatherapp.model.TemperatureData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.Mockito.verify;

public class KafkaProducerServiceTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private KafkaProducerService kafkaProducerService;

    private TemperatureData temperatureData;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        temperatureData = new TemperatureData(null, 40.7128, -74.0060, 25.5, null);
    }

    @Test
    void testSendTemperatureDataMessage() {
        kafkaProducerService.sendTemperatureDataMessage(temperatureData);
        verify(kafkaTemplate).send("my_Topic", temperatureData);
    }
}
