package com.example.currencyconverterapp

import com.example.currencyconverterapp.data.local.models.CurrencyRatesEntity
import com.example.currencyconverterapp.data.model.CurrenciesDTO
import com.example.currencyconverterapp.data.model.ExchangeRatesDTO

class MockTestUtil {
    companion object {
        fun getMockCurrencyDTO(): CurrenciesDTO {
            var currencies: HashMap<String, String> = HashMap()
            currencies.put("PKR","Pakistani Rupees")
            currencies.put("AFG","Afghani")
            return CurrenciesDTO(currencies,true)
        }

        fun getMockCurrencyRates(): ExchangeRatesDTO {
            var currencies: HashMap<String, Double> = HashMap()
            currencies.put("PKR",12.3)
            currencies.put("AFG",32.5)
            return ExchangeRatesDTO(currencies,"PKR",true,1231231231)
        }

    }
}
