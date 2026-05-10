package com.example.coursemobile.ui.screens

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.coursemobile.data.*
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val cityRepository: CityRepository,
    private val weatherRepository: WeatherRepository
) : ViewModel() {

    val cities = MutableLiveData<List<CityDto>>()
    val forecast = MutableLiveData<WeatherResponse>()
    val selectedHour = MutableLiveData<Int?>()
    val selectedDay = MutableLiveData<Int?>()
    var loadedCityKey: String? = null


    sealed class ResponseState {
        object Loading : ResponseState()
        object Error: ResponseState()
        object Success: ResponseState()
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
                responseState.value = ResponseState.Success
            }
            catch (e: Exception){
                android.util.Log.e("debugging", "ошибка: ${e.message}")
                responseState.value = ResponseState.Error
            }
        }
    }
    fun loadForecastAgain() {
        val city = cities.value?.firstOrNull()
        selectedDay.value = null
        selectedHour.value = null
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
                responseState.value = ResponseState.Success
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