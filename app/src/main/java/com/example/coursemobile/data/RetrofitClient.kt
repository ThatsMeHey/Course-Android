package com.example.coursemobile.data

import com.example.coursemobile.BuildConfig
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private val client = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("X-Api-Key", BuildConfig.API_NINJAS_KEY)
                .build()
            chain.proceed(request)
        }
        .build()

    val cityApi: CityApi = Retrofit.Builder()
        .baseUrl("https://api.api-ninjas.com/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(CityApi::class.java)

    val weatherApi: WeatherApi = Retrofit.Builder()
        .baseUrl("https://api.open-meteo.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(WeatherApi::class.java)
}