package com.snapone.weatherproject.data.datasources.openweather

import com.snapone.weatherproject.base.SingleMapper
import com.snapone.weatherproject.data.models.WeatherResponse
import com.snapone.weatherproject.domain.City
import com.snapone.weatherproject.domain.ForecastData
import com.snapone.weatherproject.utils.API_KEY
import com.snapone.weatherproject.utils.LANG
import com.snapone.weatherproject.utils.UNITS
import io.mockk.*
import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import retrofit2.Response


class CityInfoServiceTest {

    private lateinit var apiClient: ApiClient
    private lateinit var dispatcher: CoroutineDispatcher
    private lateinit var mapper: SingleMapper<WeatherResponse, ForecastData>
    private lateinit var cityInfoService: CityInfoService

    private val mockCity = City("Sabac", latitude = 10f, longitude = 20f, "Serbia", "RS")
    private val mockWeatherResponse = mockk<WeatherResponse>()
    private val mockForecastData = mockk<ForecastData>()

    @Before
    fun setup() {
        apiClient = mockk()
        dispatcher = Dispatchers.Unconfined
        mapper = mockk()

        cityInfoService = CityInfoService(apiClient, dispatcher, mapper)
    }

    @Test
    fun `getCityWeatherInfo fetches data and maps correctly`() = runTest {
        val mockResponse = Response.success(mockWeatherResponse)
        coEvery { apiClient.getCityInfo(any(), any(), any(), any(), any()) } returns mockResponse
        every { mapper.map(mockWeatherResponse) } returns mockForecastData

        val result = cityInfoService.getCityWeatherInfo(mockCity)

        assertEquals(mockForecastData, result)
        coVerify { apiClient.getCityInfo(mockCity.latitude, mockCity.longitude, API_KEY, UNITS, LANG) }
        verify { mapper.map(mockWeatherResponse) }
    }

    @Test
    fun `getCityWeatherInfo throws NullPointerException when response body is null`() = runTest {
        val mockResponse = Response.success<WeatherResponse>(null)
        coEvery { apiClient.getCityInfo(any(), any(), any(), any(), any()) } returns mockResponse

    }
}
