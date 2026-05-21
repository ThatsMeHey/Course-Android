package com.example.coursemobile.data

import javax.inject.Inject

class CityRepository @Inject constructor(
    private val api: CityApi
) {
    suspend fun getCity(cityName: String): List<CityDto> {
        return api.getCity(cityName)
    }
}