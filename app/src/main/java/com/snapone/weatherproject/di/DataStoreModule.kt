package com.snapone.weatherproject.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.snapone.weatherproject.WeatherApplication
import com.snapone.weatherproject.ui.helpers.DataStoreHelper


private const val USER_PREFERENCES = "USER_PREFERENCES"

object DataStoreModule {

    private fun providePreferencesDataStore(appContext: Context): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(produceFile = {
            appContext.preferencesDataStoreFile(
                USER_PREFERENCES
            )
        })
    }

    /**
     * Provides an instance of DataStoreHelper initialized with the application's shared preferences.
     *
     * @see DataStoreHelper for details on managing application-specific preferences.
     * @see providePreferencesDataStore to initialize the preferences data store.
     *
     * @param singleton instance of the application used to access the context.
     * @return A configured DataStoreHelper instance for managing preferences.
     */
    val dataStoreHelper = DataStoreHelper(providePreferencesDataStore(WeatherApplication.instance))

}