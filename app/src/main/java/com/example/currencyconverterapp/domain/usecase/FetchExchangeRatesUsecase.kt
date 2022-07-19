package com.example.currencyconverterapp.domain.usecase

import com.example.currencyconverterapp.data.local.models.CurrencyRatesEntity
import com.example.currencyconverterapp.data.local.repository.LocalRepository
import com.example.currencyconverterapp.data.model.toDataBaseModel
import com.example.currencyconverterapp.data.remote.DataState
import com.example.currencyconverterapp.data.repository.Repository
import com.example.currencyconverterapp.domain.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class FetchExchangeRatesUsecase @Inject constructor(
    private val repository: Repository,
    private val localRepo: LocalRepository
) {

    @ExperimentalCoroutinesApi
    suspend operator fun invoke(
        source: String,
        amount: Double
    ): Flow<DataState<List<CurrencyRatesEntity>>> {
        return flow {

            repository.getExchangeRates().collect { response ->
                when (response) {
                    is DataState.Success -> {
                        var convertedList: List<CurrencyRatesEntity> = ArrayList()

                        if (response.data?.isNotEmpty() == true) {
                            val currencyValue =
                                getCurrencyValue(response.data, amount, source)
                            convertedList = createConvertedList(response.data, currencyValue)
                        }
                        emit(DataState.Success(convertedList))
                    }
                    else -> {

                    }
                }
            }

        }.flowOn(Dispatchers.IO)
    }


    private fun createConvertedList(
        list: List<CurrencyRatesEntity>,
        currencyValue: Double
    ): List<CurrencyRatesEntity> {
        val convertedList: MutableList<CurrencyRatesEntity> = ArrayList()
        list.forEach {
            val obj = CurrencyRatesEntity(
                currencyName = if (it.currencyName == Constants.REMOVE_SOURCE_STRING_FOR_USD) {
                    Constants.DEFAULT_SOURCE_CURRENCY
                } else it.currencyName.replace(
                    Constants.DEFAULT_SOURCE_CURRENCY,
                    ""
                ),
                currencyExchangeValue = String.format(
                    "%.3f",
                    it.currencyExchangeValue * currencyValue
                ).toDouble()
            )
            convertedList.add(obj)
        }
        return convertedList
    }

    private fun getCurrencyValue(list: List<CurrencyRatesEntity>, amount: Double, source: String) =
        amount / list.filter {
            it.currencyName.contains(source)
        }.map {
            it.currencyExchangeValue
        }.first()

}
