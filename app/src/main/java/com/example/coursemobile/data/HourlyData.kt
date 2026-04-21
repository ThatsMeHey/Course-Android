package com.example.coursemobile.data

import com.google.gson.annotations.SerializedName

data class HourlyData(
    @SerializedName("time") val time: List<String>,
    @SerializedName("temperature_2m") val temperature: List<Double>,
    @SerializedName("relative_humidity_2m") val humidity: List<Int>,
    @SerializedName("weathercode") val weathercode: List<Int>
)