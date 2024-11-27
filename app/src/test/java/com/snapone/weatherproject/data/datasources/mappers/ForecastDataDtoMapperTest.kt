package com.snapone.weatherproject.data.datasources.mappers

import com.snapone.weatherproject.data.mappers.ForecastDataDtoMapper
import com.snapone.weatherproject.data.models.Clouds
import com.snapone.weatherproject.data.models.Coord
import com.snapone.weatherproject.data.models.Main
import com.snapone.weatherproject.data.models.Rain
import com.snapone.weatherproject.data.models.Snow
import com.snapone.weatherproject.data.models.Sys
import com.snapone.weatherproject.data.models.Weather
import com.snapone.weatherproject.data.models.WeatherResponse
import com.snapone.weatherproject.data.models.Wind
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ForecastDataDtoMapperTest {

    private lateinit var forecastDataDtoMapper: ForecastDataDtoMapper

    private val mockWeatherResponse = WeatherResponse(
        coord = Coord(12.34, 56.78),
        weather = listOf(Weather(id = 1, main = "Clear", description = "Clear sky", icon = "01d")),
        base = "stations",
        main = Main(temp = 25.0, feels_like = 24.0, temp_min = 18.0, temp_max = 30.0, pressure = 1012, humidity = 60, sea_level = null, grnd_level = null),
        visibility = 10000,
        wind = Wind(speed = 5.0, deg = 200, gust = 10.0),
        rain = Rain(`1h` = 2.5),
        snow = Snow(`1h` = 2.5),
        clouds = Clouds(all = 0),
        dt = 1638316800,
        sys = Sys(type = 1, id = 1, country = "US", sunrise = 1638295600, sunset = 1638347600),
        timezone = -18000,
        id = 1,
        name = "Sabac",
        cod = 200
    )

    private val mockWeatherResponseNoRain = WeatherResponse(
        coord = Coord(12.34, 56.78),
        weather = listOf(Weather(id = 1, main = "Partly Cloudy", description = "Partly cloudy", icon = "02d")),
        base = "stations",
        main = Main(temp = 22.0, feels_like = 21.0, temp_min = 16.0, temp_max = 28.0, pressure = 1015, humidity = 55, sea_level = null, grnd_level = null),
        visibility = 10000,
        wind = Wind(speed = 3.0, deg = 180, gust = 5.0),
        rain = null,
        snow = Snow(`1h` = 2.5),
        clouds = Clouds(all = 50),
        dt = 1638316800,
        sys = Sys(type = 1, id = 1, country = "US", sunrise = 1638295600, sunset = 1638347600),
        timezone = -18000,
        id = 1,
        name = "Sabac",
        cod = 200
    )

    @Before
    fun setup() {
        forecastDataDtoMapper = ForecastDataDtoMapper()
    }

    @Test
    fun `map extracts weather data correctly with rain data`() {
        val result = forecastDataDtoMapper.map(mockWeatherResponse)

        assertEquals("Clear sky", result.weather)
        assertEquals("01d", result.icon)
        assertEquals(25, result.currentTemperature)
        assertEquals(18, result.low)
        assertEquals(30, result.high)
        assertEquals(2, result.precipitationLevel)  // Rain is 2.5mm, should be converted to 2
    }

    @Test
    fun `map handles null rain data correctly`() {
        val result = forecastDataDtoMapper.map(mockWeatherResponseNoRain)

        assertEquals("Partly cloudy", result.weather)
        assertEquals("02d", result.icon)
        assertEquals(22, result.currentTemperature)
        assertEquals(16, result.low)
        assertEquals(28, result.high)
        assertEquals(2, result.precipitationLevel)
    }

    @Test
    fun `map returns default values when weather or icon is missing`() {
        val mockEmptyWeatherResponse = WeatherResponse(
            coord = Coord(12.34, 56.78),
            weather = emptyList(),
            base = "stations",
            main = Main(temp = 20.0, feels_like = 18.0, temp_min = 15.0, temp_max = 25.0, pressure = 1013, humidity = 60, sea_level = null, grnd_level = null),
            visibility = 10000,
            wind = Wind(speed = 5.0, deg = 200, gust = 10.0),
            rain = Rain(`1h` = 0.0),
            snow = Snow(`1h` = 0.0),
            clouds = Clouds(all = 0),
            dt = 1638316800,
            sys = Sys(type = 1, id = 1, country = "US", sunrise = 1638295600, sunset = 1638347600),
            timezone = -18000,
            id = 1,
            name = "Sabac",
            cod = 200
        )

        val result = forecastDataDtoMapper.map(mockEmptyWeatherResponse)

        assertEquals("Unknown weather", result.weather)
        assertEquals("", result.icon)
        assertEquals(20, result.currentTemperature)
        assertEquals(15, result.low)
        assertEquals(25, result.high)
        assertEquals(0, result.precipitationLevel)
    }
}
