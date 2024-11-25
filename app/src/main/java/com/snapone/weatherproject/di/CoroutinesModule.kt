package com.snapone.weatherproject.di

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

object CoroutinesModule {

    fun provideCoroutineDispatcher(): CoroutineDispatcher = Dispatchers.IO

    fun provideDefaultCoroutineDispatcher(): CoroutineDispatcher = Dispatchers.Default

}