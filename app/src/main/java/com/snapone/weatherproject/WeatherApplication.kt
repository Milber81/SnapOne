package com.snapone.weatherproject

import android.app.Application

class WeatherApplication : Application() {

    companion object {
        lateinit var instance: WeatherApplication
            private set
    }

    override fun onCreate() {
        super.onCreate()

        instance = this
    }
}