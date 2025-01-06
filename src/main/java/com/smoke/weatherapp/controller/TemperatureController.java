package com.smoke.weatherapp.controller;

import com.smoke.weatherapp.model.TemperatureData;
import com.smoke.weatherapp.model.TemperatureResponse;
import com.smoke.weatherapp.service.TemperatureService;
import com.smoke.weatherapp.util.Constants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for handling temperature data requests.
 */
@RestController
@Tag(name = "Temperature", description = "Endpoints for temperature data")
public class TemperatureController {

    @Autowired
    private TemperatureService temperatureService;

    /**
     * Fetches the current temperature for the given coordinates.
     * If the data is cached and it has not expired, it will be returned from the cache.
     *
     * @param latitude  Latitude of the location.
     * @param longitude Longitude of the location.
     * @return TemperatureResponse containing the temperature data.
     */
    @GetMapping("/temperature")
    @Operation(summary = "Get temperature data", description = "Fetches the current temperature for the given coordinates. If the data is cached and it has not expired, it will be returned from the cache.")
    public TemperatureResponse getTemperature(
            @Parameter(description = "Latitude of the location", required = true) @RequestParam @Min(Constants.MIN_LATITUDE_LONG) @Max(Constants.MAX_LATITUDE_LONG) double latitude,
            @Parameter(description = "Longitude of the location", required = true) @RequestParam @Min(Constants.MIN_LONGITUDE_LONG) @Max(Constants.MAX_LONGITUDE_LONG) double longitude) {
        TemperatureData temperatureData = temperatureService.getTemperatureData(latitude, longitude);
        return new TemperatureResponse(temperatureData.getLatitude(), temperatureData.getLongitude(), temperatureData.getTemperature());
    }

    /**
     * Deletes the temperature data for the given coordinates from the database.
     *
     * @param latitude  Latitude of the location.
     * @param longitude Longitude of the location.
     * @return boolean indicating success or failure.
     */
    @DeleteMapping("/temperature")
    @Operation(summary = "Delete temperature data", description = "Deletes the temperature data for the given coordinates from the database.")
    public boolean deleteTemperature(
            @Parameter(description = "Latitude of the location", required = true) @RequestParam @Min(Constants.MIN_LATITUDE_LONG) @Max(Constants.MAX_LATITUDE_LONG) double latitude,
            @Parameter(description = "Longitude of the location", required = true) @RequestParam @Min(Constants.MIN_LONGITUDE_LONG) @Max(Constants.MAX_LONGITUDE_LONG) double longitude) {
        return temperatureService.deleteTemperatureData(latitude, longitude);
    }
}
