package com.snapone.weatherproject.domain.repositories

import com.snapone.weatherproject.domain.City

interface CitiesRepository {

    suspend fun getAllCities(): List<City>

    suspend fun addCity(city: City)

    suspend fun removeCity(city: City)
}