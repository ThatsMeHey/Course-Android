package com.example.coursemobile.data

import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    @SerializedName("hourly") val hourly: HourlyData,
    @SerializedName("utc_offset_seconds") val utcOffsetSeconds: Int
)