package com.snapone.weatherproject.usecases

import com.snapone.weatherproject.data.models.WeatherResponse
import com.snapone.weatherproject.domain.repositories.CityInfoRepository

class GetCityInfoUseCase(
    private val repository: CityInfoRepository
) {
    suspend fun getCityInfo(latitude: Float, longitude: Float): WeatherResponse =
        repository.getCityInfo(latitude, longitude)
}
