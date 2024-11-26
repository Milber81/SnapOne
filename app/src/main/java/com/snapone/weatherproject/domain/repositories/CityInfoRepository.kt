package com.snapone.weatherproject.domain.repositories

import com.snapone.weatherproject.domain.City
import com.snapone.weatherproject.domain.ForecastData

fun interface CityInfoRepository{
    suspend fun getCityWeatherInfo(city: City): ForecastData
}

