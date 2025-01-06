package com.smoke.weatherapp.service;

import com.smoke.weatherapp.model.TemperatureData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    private static final String TOPIC = "my-Topic";

    public void sendTemperatureDataMessage(TemperatureData data) {
        kafkaTemplate.send(TOPIC, data);
    }
}
