package com.snapone.weatherproject.ui.main

import com.snapone.weatherproject.base.ListMapper
import com.snapone.weatherproject.base.Merger
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

class CityMerger : Merger<ForecastData, City> {
    override fun merge(item1: ForecastData, item2: City): City {
        // Update the City object with the new forecastData
        val itm = item2.copy(forecastData = item1)
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