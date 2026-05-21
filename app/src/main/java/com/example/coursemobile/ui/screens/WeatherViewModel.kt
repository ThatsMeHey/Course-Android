package com.example.coursemobile.ui.screens

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.coursemobile.data.*
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val cityRepository: CityRepository,
    private val weatherRepository: WeatherRepository
) : ViewModel() {

    val cities = MutableLiveData<List<CityDto>>()
    private val _uiState = MutableStateFlow(WeatherUiState())
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()
    lateinit var cityKey: String


    sealed class ResponseState {
        object Loading : ResponseState()
        object Error: ResponseState()
        object Success: ResponseState()
    }
    fun selectDay(day: Int) {
        _uiState.update { it.copy(selectedDay = day) }
    }
    fun selectHour(hour: Int) {
        _uiState.update { it.copy(selectedHour = hour) }
    }

    fun loadCities(name: String) {
        cityKey = name
        _uiState.update { it.copy(responseState = ResponseState.Loading) }
        viewModelScope.launch {
            try {
                val result = cityRepository.getCity(name)
                cities.value = result
            } catch (e: Exception) {
                android.util.Log.e("debugging", "ошибка: ${e.message}")
                _uiState.update { it.copy(responseState = ResponseState.Error) }
            }
        }
    }

    fun loadForecast(city: CityDto) {
        _uiState.update { it.copy(responseState = ResponseState.Loading) }
        viewModelScope.launch {
            try {
                val today = LocalDate.now()
                val endDate = today.plusDays(6)
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                _uiState.update { it.copy(forecast = weatherRepository.getForecastForWeek(
                    city.latitude,
                    city.longitude,
                    today.format(formatter),
                    endDate.format(formatter)
                ))}
                val date = LocalDateTime.now(
                    ZoneOffset.ofTotalSeconds(uiState.value.forecast!!.utcOffsetSeconds)
                )
                _uiState.update { it.copy(selectedDay = 0) }
                val hourFormatter = DateTimeFormatter.ofPattern("HH", Locale.getDefault())
                _uiState.update { it.copy(selectedHour =  date.format(hourFormatter).toInt()) }

                _uiState.update { it.copy(responseState = ResponseState.Success) }
            }
            catch (e: Exception){
                android.util.Log.e("debugging", "ошибка: ${e.message}")
                _uiState.update { it.copy(responseState = ResponseState.Error) }
            }
        }
    }
    fun loadForecastAgain() {
        if (cities.value?.firstOrNull() == null) loadCities(cityKey)

        val city = cities.value?.firstOrNull()
        if (city != null) loadForecast(city)
    }
    fun groupByDay(hourly: HourlyData): Map<String, List<Int>> {
        return hourly.time.indices.groupBy { i ->
            hourly.time[i].substringBefore("T")
        }
    }
}