package com.example.coursemobile.data

import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    @GET("v1/forecast")
    suspend fun getForecastForWeek(
        @Query("latitude") lat: Double,
        @Query("longitude") lon: Double,
        @Query("hourly") hourly: String = "temperature_2m,relative_humidity_2m,weathercode,windspeed_10m,winddirection_10m,pressure_msl",
        @Query("windspeed_unit") windspeedUnit: String = "kmh",
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String,
        @Query("timezone") timezone: String = "auto"
    ): WeatherResponse
}