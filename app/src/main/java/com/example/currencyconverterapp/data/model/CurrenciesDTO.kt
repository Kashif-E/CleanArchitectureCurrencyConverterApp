package com.example.currencyconverterapp.data.model

import com.example.currencyconverterapp.data.local.models.CurrencyNamesEntity
import com.example.currencyconverterapp.data.local.models.asDomainModel
import com.example.currencyconverterapp.domain.domain_models.CurrencyNameDomainModel
import com.google.gson.annotations.SerializedName

data class CurrenciesDTO(
    @SerializedName("currencies")
    var currencies: HashMap<String, String> = HashMap(),
    @SerializedName("success")
    val success: Boolean,
)

fun CurrenciesDTO.asEntities(): List<CurrencyNamesEntity> {
    return getModelFromResponse()

}

fun CurrenciesDTO.asDomainModel(): List<CurrencyNameDomainModel> {
    return getModelFromResponse().asDomainModel()
}

private fun CurrenciesDTO.getModelFromResponse(): MutableList<CurrencyNamesEntity> {
    val exchangeRatelist: MutableList<CurrencyNamesEntity> = ArrayList()
    currencies.map { pair ->
        CurrencyNamesEntity(
            pair.key,
            pair.value
        )
    }.run {
        exchangeRatelist.addAll(this)
    }
    return exchangeRatelist
}