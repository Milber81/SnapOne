package com.snapone.weatherproject.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.snapone.weatherproject.domain.City

class SharedViewModel : ViewModel() {
    var data = MutableLiveData<City>()
}