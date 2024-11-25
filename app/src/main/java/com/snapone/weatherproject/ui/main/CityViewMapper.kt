package com.snapone.weatherproject.ui.main

import com.snapone.weatherproject.base.ListMapper
import com.snapone.weatherproject.base.Merger
import com.snapone.weatherproject.data.models.WeatherResponse
import com.snapone.weatherproject.domain.City
import com.snapone.weatherproject.domain.ForecastData

data class CityViewItem(
    val id: Int,
    val name: String,
    val icon: String,
    val temperature: String
) {
    // Override equals and hashCode to compare content rather than reference
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as CityViewItem
        return name == other.name
    }

    override fun hashCode(): Int {
        return id
    }
}

class CityMerger : Merger<WeatherResponse, City> {
    override fun merge(item1: WeatherResponse, item2: City): City {
        // Map the weather data from the WeatherResponse to the City forecastData
        val weatherDescription = item1.weather.firstOrNull()?.description ?: "Unknown weather"
        val icon = item1.weather.firstOrNull()?.icon ?: ""
        val currentTemperature = item1.main.temp
        val lowTemperature = item1.main.temp_min
        val highTemperature = item1.main.temp_max
        val precipitation = item1.rain?.`1h`?.toInt() ?: 0 // rain in last 1 hour (default to 0 if not available)

        // Create the forecastData based on the weather data from the response
        val forecastData = ForecastData(
            weather = weatherDescription,
            icon = icon,
            currentTemperature = currentTemperature.toInt(),
            low = lowTemperature.toInt(),
            high = highTemperature.toInt(),
            precipitation = precipitation
        )

        // Update the City object with the new forecastData
        val itm = item2.copy(forecastData = forecastData)
        return itm
    }
}

class CityViewMapper {
    fun map(city: City): CityViewItem {
        val temperature = city.forecastData?.currentTemperature?.let {
            "$it°C"
        } ?: "N/A"
        val icon = city.forecastData?.icon ?: ""
        return CityViewItem(
            id = city.hashCode(),
            name = city.name,
            icon = icon,
            temperature = temperature
        )
    }
}


class CityListViewMapper: ListMapper<City, CityViewItem> {

    private val cityViewMapper = CityViewMapper()

    override fun map(items: List<City>): List<CityViewItem> {
        return items.map {
            cityViewMapper.map(it)
        }
    }
}