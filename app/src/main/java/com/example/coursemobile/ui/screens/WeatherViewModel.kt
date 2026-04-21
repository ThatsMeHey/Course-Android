package com.example.coursemobile.ui.screens

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.coursemobile.data.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class WeatherViewModel : ViewModel() {
    private val cityRepository = CityRepository()
    private val weatherRepository = WeatherRepository()

    val cities = MutableLiveData<List<CityDto>>()
    val forecast = MutableLiveData<WeatherResponse>()


    sealed class ResponseState {
        object Loading : ResponseState()
        object Error: ResponseState()
    }

    val responseState = MutableLiveData<ResponseState>()

    fun loadCities(name: String) {
        responseState.value = ResponseState.Loading
        viewModelScope.launch {
            try {
                val result = cityRepository.getCity(name)
                cities.value = result
            } catch (e: Exception) {
                android.util.Log.e("debugging", "ошибка: ${e.message}")
                responseState.value = ResponseState.Error
            }
        }
    }

    fun loadForecast(city: CityDto) {
        responseState.value = ResponseState.Loading
        viewModelScope.launch {
            try {
                val today = LocalDate.now()
                val endDate = today.plusDays(6)
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                forecast.value = weatherRepository.getForecastForWeek(
                    city.latitude,
                    city.longitude,
                    today.format(formatter),
                    endDate.format(formatter)
                )
            }
            catch (e: Exception){
                android.util.Log.e("debugging", "ошибка: ${e.message}")
                responseState.value = ResponseState.Error
            }
        }
    }
    fun loadForecastAgain() {
        val city = cities.value?.firstOrNull()
        responseState.value = ResponseState.Loading
        viewModelScope.launch {
            try {
                val today = LocalDate.now()
                val endDate = today.plusDays(6)
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                forecast.value = weatherRepository.getForecastForWeek(
                    city!!.latitude,
                    city.longitude,
                    today.format(formatter),
                    endDate.format(formatter)
                )
            }
            catch (e: Exception){
                android.util.Log.e("debugging", "ошибка: ${e.message}")
                responseState.value = ResponseState.Error
            }
        }
    }
    fun groupByDay(hourly: HourlyData): Map<String, List<Int>> {
        return hourly.time.indices.groupBy { i ->
            hourly.time[i].substringBefore("T")
        }
    }
}