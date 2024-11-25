package com.snapone.weatherproject.data.datasources.openweather

import com.snapone.weatherproject.data.models.WeatherResponse
import com.snapone.weatherproject.domain.repositories.CityInfoRepository
import com.snapone.weatherproject.utils.API_KEY
import com.snapone.weatherproject.utils.LANG
import com.snapone.weatherproject.utils.UNITS
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import retrofit2.Response

/**
 * Service implementation for fetching city information from a remote API.
 *
 * This class interacts with the [ApiClient] to fetch city details based on latitude and longitude
 * and implements the [CityInfoRepository] interface for abstraction and testability.
 *
 * @param api The API client used for network requests.
 * @param dispatcher The [CoroutineDispatcher] for managing coroutine execution (e.g., Dispatchers.IO for I/O tasks).
 */
class CityInfoService(
    private val api: ApiClient,
    private val dispatcher: CoroutineDispatcher
) : CityInfoRepository {

    /**
     * Fetches city information based on the provided latitude and longitude.
     *
     * @param latitude The latitude of the city.
     * @param longitude The longitude of the city.
     * @return A [WeatherResponse] object containing details of the city.
     * @throws NullPointerException if the API response body is null.
     */
    override suspend fun getCityInfo(latitude: Float, longitude: Float): WeatherResponse {
        return withContext(dispatcher) { // Ensures network operations run on the specified dispatcher.
            val response: Response<WeatherResponse> =
                api.getCityInfo(latitude, longitude, API_KEY, UNITS, LANG)
            response.body()!! // Assumes a non-null response body. Ensure API guarantees this or handle null cases.
        }
    }
}
