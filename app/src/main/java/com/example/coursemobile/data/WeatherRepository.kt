package com.example.coursemobile.data

import javax.inject.Inject

class WeatherRepository @Inject constructor(
    private val api: WeatherApi
) {
    suspend fun getForecastForWeek(
        lat: Double,
        lon: Double,
        startDate: String,
        endDate: String
    ): WeatherResponse {
        return api.getForecastForWeek(
            lat = lat,
            lon = lon,
            hourly = "temperature_2m,relative_humidity_2m,weathercode,windspeed_10m,winddirection_10m,pressure_msl",
            startDate = startDate,
            endDate = endDate,
            timezone = "auto"
        )
    }
}