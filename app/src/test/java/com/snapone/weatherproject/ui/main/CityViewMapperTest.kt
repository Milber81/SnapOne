package com.snapone.weatherproject.ui.main

import com.snapone.weatherproject.domain.City
import com.snapone.weatherproject.domain.ForecastData
import org.junit.Assert.assertEquals
import org.junit.Test


class CityViewMapperTest {

    private val cityViewMapper = CityViewMapper()

    @Test
    fun `map should correctly transform City to CityViewItem with forecastData`() {
        val forecastData = ForecastData(
            weather = "Clear sky",
            icon = "01d",
            currentTemperature = 25,
            low = 18,
            high = 30,
            precipitation = 2
        )

        val city = City(
            latitude = 12.34f,
            longitude = 56.78f,
            name = "Sabac",
            country = "Serbia",
            countryAbbr = "RS",
            forecastData = forecastData
        )

        val cityViewItem = cityViewMapper.map(city)

        assertEquals(city.hashCode(), cityViewItem.id)
        assertEquals("Sabac", cityViewItem.name)
        assertEquals("25°C", cityViewItem.temperature)
        assertEquals("01d", cityViewItem.icon)
    }

    @Test
    fun `map should handle City with no forecastData gracefully`() {
        val city = City(
            latitude = 12.34f,
            longitude = 56.78f,
            name = "Sabac",
            country = "Serbia",
            countryAbbr = "RS"
        )

        val cityViewItem = cityViewMapper.map(city)

        assertEquals(city.hashCode(), cityViewItem.id)
        assertEquals("Sabac", cityViewItem.name)
        assertEquals("N/A", cityViewItem.temperature)
        assertEquals("", cityViewItem.icon)
    }
}
