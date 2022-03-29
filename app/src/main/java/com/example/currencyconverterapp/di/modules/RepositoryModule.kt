package com.example.currencyconverterapp.di.modules

import android.app.Application
import com.example.currencyconverterapp.data.remote.ApiService
import com.example.currencyconverterapp.data.repository.Repository
import com.example.currencyconverterapp.data.repository.RepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * The Dagger Module for providing repository instances.
 */
@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {


    @Singleton
    @Provides
    fun provideRepository( apiService: ApiService): Repository {
        return RepositoryImpl( apiService)
    }
}
