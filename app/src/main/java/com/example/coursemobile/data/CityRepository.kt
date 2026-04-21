package com.example.coursemobile.data

class CityRepository {
    private val api = RetrofitClient.cityApi

    suspend fun getCity(cityName: String): List<CityDto> {
        return api.getCity(cityName)
    }
}