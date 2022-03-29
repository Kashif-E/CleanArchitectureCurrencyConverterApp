package com.example.currencyconverterapp.data.local.repository

import com.example.currencyconverterapp.data.local.dao.CurrenciesDao
import com.example.currencyconverterapp.data.local.models.CurrencyNamesEntity
import com.example.currencyconverterapp.data.local.models.CurrencyRatesEntity
import javax.inject.Inject

class LocalRepository @Inject constructor(var currenciesDao: CurrenciesDao) {

    fun insertCurrencyNames(currencyNameEntities: List<CurrencyNamesEntity>) {
        currenciesDao.insertCurrencyNames(currencyNameEntities)
    }

    fun getAllCurrencyNames(): List<CurrencyNamesEntity> {
        return currenciesDao.getAllCurrencyNames()
    }

    fun insertCurrencyRates(currencyRateEntities: List<CurrencyRatesEntity>) {
        currenciesDao.insertCurrencyRates(currencyRateEntities)
    }

    fun getAllCurrencyRates(): List<CurrencyRatesEntity> {
        return currenciesDao.getAllCurrencyRates()
    }
}