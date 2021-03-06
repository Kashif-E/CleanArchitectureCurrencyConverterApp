package com.example.currencyconverterapp.data.repository

import com.example.currencyconverterapp.data.local.models.CurrencyRatesEntity
import com.example.currencyconverterapp.data.model.ExchangeRatesDTO
import com.example.currencyconverterapp.data.remote.DataState
import com.example.currencyconverterapp.domain.domain_models.CurrencyNameDomainModel
import kotlinx.coroutines.flow.Flow

/**
 * Repository is an interface data layer to handle communication with any data source such as Server or local database.
 * @see [RepositoryImpl] for implementation of this class to utilize APIService.
 */
interface Repository {

    suspend fun getCurrencies(): Flow<DataState<List<CurrencyNameDomainModel>>>
    suspend fun getExchangeRates(): Flow<DataState<List<CurrencyRatesEntity>>>
}
