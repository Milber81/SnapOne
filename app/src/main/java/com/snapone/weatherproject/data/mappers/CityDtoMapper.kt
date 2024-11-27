package com.snapone.weatherproject.data.mappers

import com.snapone.weatherproject.base.SingleMapper
import com.snapone.weatherproject.data.models.WeatherResponse
import com.snapone.weatherproject.domain.ForecastData

class ForecastDataDtoMapper : SingleMapper<WeatherResponse, ForecastData> {
    override fun map(item: WeatherResponse): ForecastData {
        val weatherDescription = item.weather.firstOrNull()?.description ?: "Unknown weather"
        val icon = item.weather.firstOrNull()?.icon ?: ""
        val currentTemperature = item.main.temp
        val lowTemperature = item.main.temp_min
        val highTemperature = item.main.temp_max
        val precipitationLevel = item.rain?.`1h`?.toInt() ?: (item.snow?.`1h`?.toInt() ?: 0)
        val precipitationType = item.rain?.let { "Rain" } ?: "Snow"

        val forecastData = ForecastData(
            weather = weatherDescription,
            icon = icon,
            currentTemperature = currentTemperature.toInt(),
            low = lowTemperature.toInt(),
            high = highTemperature.toInt(),
            precipitationLevel = precipitationLevel,
            precipitationType = precipitationType
        )

        return forecastData
    }
}
