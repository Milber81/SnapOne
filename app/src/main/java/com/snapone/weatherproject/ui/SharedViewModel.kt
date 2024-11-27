package com.snapone.weatherproject.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.snapone.weatherproject.domain.City
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class SharedViewModel : ViewModel() {
    private val _data = MutableSharedFlow<City?>(replay = 1)
    val data: SharedFlow<City?> get() = _data

    fun postCity(city: City?) {
        viewModelScope.launch {
            _data.emit(city)
        }
    }
}