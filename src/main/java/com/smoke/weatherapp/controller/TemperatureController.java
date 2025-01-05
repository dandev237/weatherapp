package com.smoke.weatherapp.controller;

import com.smoke.weatherapp.model.TemperatureData;
import com.smoke.weatherapp.model.TemperatureResponse;
import com.smoke.weatherapp.service.TemperatureService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Temperature", description = "Endpoints for temperature data")
public class TemperatureController {

    @Autowired
    private TemperatureService temperatureService;

    @GetMapping("/temperature")
    @Operation(summary = "Get temperature data", description = "Fetches the current temperature for the given coordinates. If the data is cached and it has not expired, it will be returned from the cache.")
    public TemperatureResponse getTemperature(
            @Parameter(description = "Latitude of the location", required = true) @RequestParam double latitude,
            @Parameter(description = "Longitude of the location", required = true) @RequestParam double longitude) {
        TemperatureData temperatureData = temperatureService.getTemperatureData(latitude, longitude);
        return new TemperatureResponse(temperatureData.getLatitude(), temperatureData.getLongitude(), temperatureData.getTemperature());
    }
}
