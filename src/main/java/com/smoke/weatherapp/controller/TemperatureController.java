package com.smoke.weatherapp.controller;

import com.smoke.weatherapp.model.TemperatureData;
import com.smoke.weatherapp.model.TemperatureResponse;
import com.smoke.weatherapp.service.TemperatureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TemperatureController {

    @Autowired
    private TemperatureService temperatureService;

    @GetMapping("/temperature")
    public TemperatureResponse getTemperature(@RequestParam double latitude, @RequestParam double longitude) {
        TemperatureData temperatureData = temperatureService.getTemperatureData(latitude, longitude);
        return new TemperatureResponse(temperatureData.getLatitude(), temperatureData.getLongitude(), temperatureData.getTemperature());
    }
}
