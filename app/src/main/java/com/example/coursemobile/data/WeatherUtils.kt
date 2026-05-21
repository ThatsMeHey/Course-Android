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

    fun getWeatherName(code: Int, hour: Int): Int {
        return when {
            code == 0 -> R.string.clearSky
            code in 1..2 -> R.string.partlyCloudy
            code == 3 -> R.string.cloudy
            code == 45 || code == 48 -> R.string.fog
            code in 51..67 -> R.string.rain
            code in 71..77 -> R.string.snow
            code in 80..82 -> R.string.shower
            code in 85..86 -> R.string.snowShower
            code in 95..99 -> R.string.thunderstorm
            else -> R.string.noData
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

    fun getWindDirection(deg: Double): Int {
        return when (deg) {
            in 0.0..22.5, in 337.5..360.0 -> R.string.north
            in 22.5..67.5 -> R.string.northEast
            in 67.5..112.5 -> R.string.east
            in 112.5..157.5 -> R.string.southEast
            in 157.5..202.5 -> R.string.south
            in 202.5..247.5 -> R.string.southWest
            in 247.5..292.5 -> R.string.west
            in 292.5..337.5 -> R.string.northWest
            else -> R.string.noData
        }
    }
}