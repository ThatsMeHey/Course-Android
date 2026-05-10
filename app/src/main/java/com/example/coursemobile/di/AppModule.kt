package com.example.coursemobile.di

import com.example.coursemobile.data.CityApi
import com.example.coursemobile.data.RetrofitClient
import com.example.coursemobile.data.WeatherApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideCityApi(): CityApi {
        return RetrofitClient.cityApi
    }

    @Provides
    @Singleton
    fun provideWeatherApi(): WeatherApi {
        return RetrofitClient.weatherApi
    }
}