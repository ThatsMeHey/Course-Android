package com.example.coursemobile.data

class WeatherRepository {
    private val api = RetrofitClient.weatherApi

    suspend fun getForecastForWeek(lat: Double, lon: Double, startDate: String, endDate: String): WeatherResponse {
        return api.getForecastForWeek(
            lat = lat,
            lon = lon,
            hourly = "temperature_2m,relative_humidity_2m,weathercode",
            startDate = startDate,
            endDate = endDate,
            timezone = "auto"
        )
    }
}