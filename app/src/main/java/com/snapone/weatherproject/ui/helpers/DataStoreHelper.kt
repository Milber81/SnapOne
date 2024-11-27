package com.snapone.weatherproject.ui.helpers

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.snapone.weatherproject.domain.City
import com.snapone.weatherproject.domain.repositories.CitiesRepository
import kotlinx.coroutines.flow.first
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json


class DataStoreHelper(
    private val dataStore: DataStore<Preferences>
) : CitiesRepository {
    companion object {
        val SAVED_CITIES = stringPreferencesKey("cities")
    }


    override suspend fun getAllCities(): List<City> {
        val defaultCityList = cities.take(3)
        val json = dataStore.data.first()[SAVED_CITIES] ?: return defaultCityList
        return Json.decodeFromString(json)
    }

    override suspend fun addCity(city: City) {
        val currentCities = getAllCities().toMutableList()
        if(currentCities.contains(city))
            return
        currentCities.add(city)
        saveCities(currentCities)
    }

    override suspend fun removeCity(city: City) {
        val currentCities = getAllCities().toMutableList()
        currentCities.remove(city)
        saveCities(currentCities)
    }

    private suspend fun saveCities(cities: List<City>) {
        val json = Json.encodeToString(ListSerializer(City.serializer()), cities)
        dataStore.edit { preferences ->
            preferences[SAVED_CITIES] = json
        }
    }
}

