package com.example.coursemobile.data

import com.example.coursemobile.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.roundToInt

object WeatherUtils {
    fun getWeatherIcon(code: Int, hour: Int): Int {
        val isDay = hour in 6..21
        return when {
            code == 0 && isDay -> R.drawable.wi_day_sunny
            code == 0 && !isDay -> R.drawable.wi_night_clear
            code in 1..2 && isDay -> R.drawable.wi_day_cloudy
            code in 1..2 && !isDay -> R.drawable.wi_night_cloudy
            code == 3 -> R.drawable.wi_cloudy
            code == 45 || code == 48 -> R.drawable.wi_fog
            code in 51..67 -> R.drawable.wi_rain
            code in 71..77 -> R.drawable.wi_snow
            code in 80..82 -> R.drawable.wi_showers
            code in 85..86 -> R.drawable.wi_snow_shower
            code in 95..99 -> R.drawable.wi_thunderstorm
            else -> R.drawable.wi_refresh
        }
    }

    fun getDailySummary(hourly: HourlyData, date: String, localDate: String): DailySummary {
        val indices = hourly.time.indices.filter {
            hourly.time[it].startsWith(date)
        }

        val dayIndices = indices.filter {
            val hour = hourly.time[it].substringAfter("T").substringBefore(":").toInt()
            hour in 6..21
        }
        val nightIndices = indices.filter {
            val hour = hourly.time[it].substringAfter("T").substringBefore(":").toInt()
            hour !in 6..21
        }

        val dateInner = LocalDate.parse(localDate.substringBefore("T"))
        val formatter = DateTimeFormatter.ofPattern("EEE", Locale.getDefault())
        val dayOfWeekInner = dateInner.format(formatter).replaceFirstChar { it.uppercase() }

        return DailySummary(
            date = date,
            dayOfWeek = dayOfWeekInner,
            dayWeatherCode = dayIndices.map { hourly.weathercode[it] }.groupingBy { it }.eachCount()
                .maxByOrNull { it.value }?.key ?: 0,
            nightWeatherCode = nightIndices.map { hourly.weathercode[it] }.groupingBy { it }
                .eachCount().maxByOrNull { it.value }?.key ?: 0,
            maxTemp = dayIndices.maxOfOrNull { hourly.temperature[it] }?.roundToInt() ?: 0,
            minTemp = nightIndices.minOfOrNull { hourly.temperature[it] }?.roundToInt() ?: 0,
            humidity = indices.map { hourly.humidity[it] }.average().roundToInt()
        )
    }
}