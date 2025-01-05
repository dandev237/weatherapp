package com.smoke.weatherapp.repository;

import com.smoke.weatherapp.model.TemperatureData;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface TemperatureDataRepository extends MongoRepository<TemperatureData, String> {

    List<TemperatureData> findByLatitudeAndLongitude(double latitude, double longitude);
}
