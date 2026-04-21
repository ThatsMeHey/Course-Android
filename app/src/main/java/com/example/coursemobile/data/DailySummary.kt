package com.example.coursemobile.data

data class DailySummary(
    val date: String,
    val dayOfWeek: String,
    val dayWeatherCode: Int,
    val nightWeatherCode: Int,
    val maxTemp: Int,
    val minTemp: Int,
    val humidity: Int
)