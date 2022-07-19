package com.example.currencyconverterapp.data.local.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.currencyconverterapp.domain.domain_models.CurrencyNameDomainModel
import com.example.currencyconverterapp.domain.utils.Constants.TABLE_CURRENCY

@Entity(tableName = TABLE_CURRENCY)
data class CurrencyNamesEntity(
    @PrimaryKey
    val currencyName: String,
    var currencyCountryName: String
)

 fun List<CurrencyNamesEntity>.asDomainModel()= map {

     CurrencyNameDomainModel(
         currencyCountryName = it.currencyCountryName,
         currencyName = it.currencyName
     )
 }

fun List<CurrencyNameDomainModel>.asEntity()= map {

    CurrencyNamesEntity(
        currencyCountryName = it.currencyCountryName,
        currencyName = it.currencyName
    )
}

