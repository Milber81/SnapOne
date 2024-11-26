package com.snapone.weatherproject.usecases

import com.snapone.weatherproject.data.models.WeatherResponse
import com.snapone.weatherproject.domain.City
import com.snapone.weatherproject.domain.ForecastData
import com.snapone.weatherproject.domain.repositories.CityInfoRepository

class GetCityInfoUseCase(
    private val repository: CityInfoRepository
) {
    suspend fun getCityInfo(city: City): ForecastData =
        repository.getCityWeatherInfo(city)
}
