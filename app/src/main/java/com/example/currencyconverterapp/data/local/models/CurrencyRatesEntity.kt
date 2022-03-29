package com.example.currencyconverterapp.data.local.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.currencyconverterapp.utils.Constants.TABLE_CURRENCY_RATES

@Entity(tableName = TABLE_CURRENCY_RATES)
data class CurrencyRatesEntity(
    @PrimaryKey
    var currencyName: String,
    var currencyExchangeValue: Double
)
