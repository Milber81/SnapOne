package com.snapone.weatherproject.ui.main

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.snapone.weatherproject.base.ListMapper
import com.snapone.weatherproject.base.Merger
import com.snapone.weatherproject.data.models.WeatherResponse
import com.snapone.weatherproject.domain.City
import com.snapone.weatherproject.domain.ForecastData
import com.snapone.weatherproject.domain.repositories.CitiesRepository
import com.snapone.weatherproject.usecases.GetCityInfoUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel(
    private val citiesRepository: CitiesRepository,
    private val getCityInfoUseCase: GetCityInfoUseCase,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val listMapper: ListMapper<City, CityViewItem>,
    private val merger: Merger<ForecastData, City>
) : ViewModel() {

    private val singleMapper = CityViewMapper()

    private val citySet: MutableSet<City> by lazy {
        mutableSetOf()
    }

    private val _city = MutableSharedFlow<City?>()
    val city: SharedFlow<City?> get() = _city

    private val _getCities = MutableStateFlow<List<CityViewItem>>(emptyList())
    val cities: StateFlow<List<CityViewItem>> get() = _getCities

    private val _cityUpdate = MutableSharedFlow<CityViewItem>()  // No state, just events
    val cityUpdate: SharedFlow<CityViewItem> get() = _cityUpdate

    private val _loadingState = MutableLiveData<Boolean>()
    val loadingState = _loadingState

    init {
        getCities()
    }

    private fun getCities() {
        viewModelScope.launch(dispatcher) {
            _loadingState.postValue(true)
            val result = citiesRepository.getAllCities()
            _getCities.emit(listMapper.map(result))
            println("ooooo vm vm $result")
            result.forEach { city ->
                fetchCityForecast(city)
            }
            _loadingState.postValue(false)
        }
    }

    private suspend fun fetchCityForecast(city: City) {
        viewModelScope.launch {
            println("ooooo ??????? $city")
            try {
                val cityInfo = getCityInfoUseCase.getCityInfo(city)
                val _city = merger.merge(cityInfo, city)
                _cityUpdate.emit(singleMapper.map(_city))
                citySet.add(_city)
            } catch (e: Exception) {
                println("oooooo ooError fetching forecast for ${city.name}: $e")
            }
        }
    }

    fun addCity(city: City) {
        viewModelScope.launch {
            citiesRepository.addCity(city)
            getCities()
        }
    }

    fun removeCity(city: CityViewItem) {
        viewModelScope.launch {
            val mCity = citiesRepository.getAllCities().firstOrNull { it.name == city.name }
            mCity?.let {
                citiesRepository.removeCity(it)
            }
            getCities()
        }
    }

    fun getCity(cityViewItem: CityViewItem){
        viewModelScope.launch {
            if(citySet.isEmpty()){
                _city.emit(null)
            }
            else {
                val mCity = citySet.first { it.name == cityViewItem.name }
                _city.emit(mCity)
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