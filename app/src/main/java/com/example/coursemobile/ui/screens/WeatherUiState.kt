package com.example.coursemobile.ui.screens

import com.example.coursemobile.data.WeatherResponse

data class WeatherUiState(
    val forecast: WeatherResponse? = null,
    val selectedDay: Int = 0,
    val selectedHour: Int = 0,
    val responseState: WeatherViewModel.ResponseState = WeatherViewModel.ResponseState.Loading
)