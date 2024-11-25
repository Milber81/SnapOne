package com.snapone.weatherproject.ui.helpers

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.snapone.weatherproject.domain.City
import com.snapone.weatherproject.domain.repositories.CitiesRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json


class DataStoreHelper(
    private val dataStore: DataStore<Preferences>
) : CitiesRepository {
    companion object {
        val FIRST_APP_START_KEY = booleanPreferencesKey("isFirstAppStart")

        val SAVED_CITIES = stringPreferencesKey("cities")
    }


    suspend fun writeIsFirstAppStart(isFirstAppStart: Boolean) {
        dataStore.edit { preferences ->
            preferences[FIRST_APP_START_KEY] = isFirstAppStart
        }
    }

    override suspend fun getAllCities(): List<City> {
        val defaultCityList = listOf(
            City(
                "San Francisco", latitude = 37.7749f,
                longitude = -122.4194f, "California", "CA"
            ),
            City(
                "New York", latitude = 40.7128f,
                longitude = -74.0060f, "New York", "NY"
            ),
            City(
                "Salt Lake City", latitude = 40.7608f,
                longitude = -111.8910f, "Utah", "UT"
            ),
        )
        val json = dataStore.data.first()[SAVED_CITIES] ?: return defaultCityList
        return Json.decodeFromString(json)
    }

    override suspend fun addCity(city: City) {
        val currentCities = getAllCities().toMutableList()
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

