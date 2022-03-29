package com.example.currencyconverterapp.repository

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.currencyconverterapp.data.local.AppDatabase
import com.example.currencyconverterapp.data.local.dao.CurrenciesDao
import com.example.currencyconverterapp.data.local.models.CurrencyNamesEntity
import junit.framework.Assert.assertNotNull
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class LocalRepositoryTest {

    private lateinit var currenciesDao: CurrenciesDao
    private lateinit var appDatabase: AppDatabase

    @Before
    fun start() {
        appDatabase = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
        currenciesDao = appDatabase.currenciesDao()
    }

    @Test
    fun insertCurrencyNamesItem() = runBlocking {
        val item = CurrencyNamesEntity(
            currencyName = "USD",
            currencyCountryName = "United States",
        )
        currenciesDao.insertCurrencyNames(listOf(item))
        val result = currenciesDao.getAllCurrencyNames()
        assertNotNull(result)
    }
}