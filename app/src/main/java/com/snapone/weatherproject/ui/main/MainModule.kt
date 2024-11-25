package com.snapone.weatherproject.ui.main

import com.snapone.weatherproject.data.datasources.openweather.CityInfoService
import com.snapone.weatherproject.di.CoroutinesModule
import com.snapone.weatherproject.di.DataStoreModule
import com.snapone.weatherproject.di.NetworkModule
import com.snapone.weatherproject.usecases.GetCityInfoUseCase

object MainModule {

    private val apiCityInfoService = CityInfoService(
        NetworkModule.provideApiClient(),
        CoroutinesModule.provideCoroutineDispatcher()
    )

    fun provideMainViewModel(): MainViewModel {
        val getCityInfoUseCase = GetCityInfoUseCase(apiCityInfoService)
        val dataStoreHelper = DataStoreModule.dataStoreHelper
        val mapper = CityListViewMapper()
        val merger = CityMerger()

        return MainViewModel(
            citiesRepository = dataStoreHelper,
            getCityInfoUseCase = getCityInfoUseCase,
            dispatcher = CoroutinesModule.provideCoroutineDispatcher(),
            mapper,
            merger
        )
    }
}