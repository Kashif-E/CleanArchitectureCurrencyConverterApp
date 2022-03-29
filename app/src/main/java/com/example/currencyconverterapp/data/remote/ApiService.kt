package com.example.currencyconverterapp.data.remote

import com.example.currencyconverterapp.BuildConfig
import com.example.currencyconverterapp.data.model.CurrenciesDTO
import com.example.currencyconverterapp.data.model.ExchangeRatesDTO
import com.example.currencyconverterapp.utils.Constants.CURRENCIES_END_POINT
import com.example.currencyconverterapp.utils.Constants.DEFAULT_SOURCE_CURRENCY
import com.example.currencyconverterapp.utils.Constants.LIVE_END_POINT
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET(LIVE_END_POINT)
    suspend fun getExchangeRates(
        @Query("source") source: String = DEFAULT_SOURCE_CURRENCY
    ): ApiResponse<ExchangeRatesDTO>

    @GET(CURRENCIES_END_POINT)
    suspend fun getCurrencies(
    ): ApiResponse<CurrenciesDTO>

}
