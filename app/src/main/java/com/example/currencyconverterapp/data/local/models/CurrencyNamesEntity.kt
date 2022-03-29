package com.example.currencyconverterapp.data.local.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.currencyconverterapp.utils.Constants.TABLE_CURRENCY

@Entity(tableName = TABLE_CURRENCY)
data class CurrencyNamesEntity(
    @PrimaryKey
    val currencyName: String,
    var currencyCountryName: String
)
