package com.snapone.weatherproject.di

import com.snapone.weatherproject.data.datasources.openweather.ApiClient
import com.snapone.weatherproject.utils.BASE_URL
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkModule {

    private fun provideRetrofit():Retrofit {
        return Retrofit.Builder().baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    /**
     * Provides an instance of the `ApiClient` interface for making network API calls.
     *
     * This function creates a Retrofit service based on the `ApiClient` interface,
     * allowing the application to communicate with the backend APIs.
     * It relies on the `provideRetrofit()` method to supply a configured Retrofit instance.
     *
     * @return An implementation of the `ApiClient` interface.
     * @see ApiClient Defines the API endpoints and their respective HTTP methods.
     */
    fun provideApiClient(): ApiClient {
        return provideRetrofit().create(ApiClient::class.java)
    }

}