package com.example.currencyconverterapp.data.model

import com.example.currencyconverterapp.data.local.models.CurrencyRatesEntity
import com.google.gson.annotations.SerializedName

data class ExchangeRatesDTO(
    @SerializedName("quotes")
    var quotes: HashMap<String, Double> = HashMap(),
    @SerializedName("source")
    val source: String,
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("timestamp")
    val timestamp: Int
)


fun ExchangeRatesDTO.toDataBaseModel(): List<CurrencyRatesEntity> {
    val exchangeRatelist: MutableList<CurrencyRatesEntity> = ArrayList()
    quotes.map { pair ->
        CurrencyRatesEntity(
            pair.key,
            pair.value
        )
    }.run {
        exchangeRatelist.addAll(this)
    }
    return exchangeRatelist
}