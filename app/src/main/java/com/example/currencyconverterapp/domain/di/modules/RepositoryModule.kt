package com.example.currencyconverterapp.domain.di.modules

import com.example.currencyconverterapp.data.local.repository.LocalRepository
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
    fun provideRepository( apiService: ApiService, localRepository: LocalRepository): Repository {
        return RepositoryImpl( apiService, localRepository)
    }
}
