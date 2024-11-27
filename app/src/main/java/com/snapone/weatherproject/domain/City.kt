package com.snapone.weatherproject.domain

import kotlinx.serialization.Serializable

@Serializable
data class City(
    val name: String,
    val latitude: Float,
    val longitude: Float,
    val country: String,
    val countryAbbr: String,
    var forecastData: ForecastData? = null
)

@Serializable
data class ForecastData(
    val weather: String,
    val icon: String,
    val currentTemperature: Int,
    val low: Int,
    val high: Int,
    val precipitationLevel: Int,
    val precipitationType: String
)
