package com.example.coursemobile.data

import retrofit2.http.GET
import retrofit2.http.Query

interface CityApi {
    @GET("v1/city")
    suspend fun getCity(
        @Query("name") name: String
    ): List<CityDto>
}