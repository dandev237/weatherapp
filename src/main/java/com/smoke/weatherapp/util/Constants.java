package com.smoke.weatherapp.util;

public class Constants {
    public static final int CACHE_INVALIDATION_TIME_MINUTES = 1;
    public static final double MAX_LATITUDE = 90.0;
    public static final double MIN_LATITUDE = -90.0;
    public static final double MAX_LONGITUDE = 180.0;
    public static final double MIN_LONGITUDE = -180.0;

    public static final String ERROR_INVALID_COORDINATES = "Invalid coordinates";
    public static final String ERROR_OPEN_METEO_API_FAILURE = "Failed to get temperature data from Open-Meteo API";
}