package com.snapone.weatherproject.ui.main

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.snapone.weatherproject.base.ListMapper
import com.snapone.weatherproject.base.Merger
import com.snapone.weatherproject.domain.City
import com.snapone.weatherproject.domain.ForecastData
import com.snapone.weatherproject.domain.repositories.CitiesRepository
import com.snapone.weatherproject.usecases.GetCityInfoUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

sealed class UpdateDataPolicy{
    data object SOURCE: UpdateDataPolicy()
    data object ADD: UpdateDataPolicy()
    data object REMOVE: UpdateDataPolicy()
}

class MainViewModel(
    private val citiesRepository: CitiesRepository,
    private val getCityInfoUseCase: GetCityInfoUseCase,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val listMapper: ListMapper<City, CityViewItem>,
    private val merger: Merger<ForecastData, City>
) : ViewModel() {

    private val citySet: MutableSet<City> by lazy {
        mutableSetOf()
    }

    private val _city = MutableSharedFlow<City?>()
    val city: SharedFlow<City?> get() = _city

    private val _getCities = MutableSharedFlow<Pair<UpdateDataPolicy, List<CityViewItem>>>()
    val cities: SharedFlow<Pair<UpdateDataPolicy, List<CityViewItem>>> get() = _getCities

    private val _loadingState = MutableLiveData<Boolean>()
    val loadingState = _loadingState

    init {
        getCities()
    }

    private fun getCities() {
        viewModelScope.launch(dispatcher) {
            _loadingState.postValue(true)
            val result = citiesRepository.getAllCities()
            _getCities.emit(Pair(UpdateDataPolicy.SOURCE, listMapper.map(result)))
            result.forEach { city ->
                fetchCityForecast(city)
            }
            _loadingState.postValue(false)
        }
    }

    private suspend fun fetchCityForecast(city: City) {
        viewModelScope.launch {
            try {
                val cityInfo = getCityInfoUseCase.getCityInfo(city)
                val element = merger.merge(cityInfo, city)
                val viewItem = listMapper.map(listOf(element))
                _getCities.emit(Pair(UpdateDataPolicy.ADD, viewItem))
                citySet.add(element)
            } catch (e: Exception) {
                println("oooooo ooError fetching forecast for ${city.name}: $e")
            }
        }
    }

    fun addCity(city: City) {
        viewModelScope.launch {
            citiesRepository.addCity(city)
            _getCities.emit(Pair(UpdateDataPolicy.ADD, listMapper.map(listOf(city))))
            fetchCityForecast(city)
        }
    }

    fun removeCity(city: CityViewItem) {
        viewModelScope.launch {
            val mCity = citiesRepository.getAllCities().firstOrNull { it.name == city.name }
            mCity?.let {
                citiesRepository.removeCity(it)
                citySet.add(it)
                _getCities.emit(Pair(UpdateDataPolicy.REMOVE, listOf(city)))
            }
        }
    }

    fun getCity(cityViewItem: CityViewItem){
        viewModelScope.launch {
            if(citySet.isEmpty()){
                _city.emit(null)
            }
            else {
                val mCity = citySet.firstOrNull { it.name == cityViewItem.name }
                mCity?.let {
                    _city.emit(mCity)
                } ?: run {
                    _city.emit(null)
                }
            }
        }
    }

    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val actNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
        return when {
            actNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            actNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            else -> false
        }
    }
}