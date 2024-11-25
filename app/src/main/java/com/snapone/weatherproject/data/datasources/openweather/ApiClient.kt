package com.snapone.weatherproject.data.datasources.openweather

import com.snapone.weatherproject.data.models.WeatherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiClient {
    @GET("/data/2.5/weather")
    suspend fun getCityInfo(
        @Query("lat") latitude: Float?,
        @Query("lon") longitude: Float?,
        @Query("appid") apiKey: String,
        @Query("units") units: String,
        @Query("lang") lang: String
    ): Response<WeatherResponse>
}
