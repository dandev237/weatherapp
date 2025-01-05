package com.smoke.weatherapp.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class OpenMeteoResponse {

    @JsonProperty("current_weather")
    private CurrentWeather currentWeather;

    @Data
    public static class CurrentWeather {
        private double temperature;
    }
}
