package com.snapone.weatherproject.domain.repositories

import com.snapone.weatherproject.data.models.WeatherResponse

fun interface CityInfoRepository{
    suspend fun getCityInfo(latitude: Float, longitude: Float): WeatherResponse
}

