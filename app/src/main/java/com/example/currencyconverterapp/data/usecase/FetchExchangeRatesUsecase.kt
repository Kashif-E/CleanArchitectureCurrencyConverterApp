package com.example.currencyconverterapp.data.usecase

import com.example.currencyconverterapp.data.local.models.CurrencyRatesEntity
import com.example.currencyconverterapp.data.local.repository.LocalRepository
import com.example.currencyconverterapp.data.model.toDataBaseModel
import com.example.currencyconverterapp.data.remote.DataState
import com.example.currencyconverterapp.data.repository.Repository
import com.example.currencyconverterapp.utils.Constants
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
            val responseFromDatabase = localRepo.getAllCurrencyRates()
            if ( responseFromDatabase.isNullOrEmpty().not()){
                var convertedList: List<CurrencyRatesEntity> = ArrayList()
                val currencyValue = getCurrencyValue(responseFromDatabase,amount,source)
                convertedList = createConvertedList(responseFromDatabase, currencyValue)
                emit(DataState.Success(convertedList))
            } else {
                val rates = repository.getExchangeRates()
                rates.collect { response ->
                    when (response) {
                        is DataState.Success -> {
                            var convertedList: List<CurrencyRatesEntity> = ArrayList()
                            var exchangeRatelist: List<CurrencyRatesEntity> = ArrayList()
                            if (response.data!!.success) {
                                exchangeRatelist = response.data.toDataBaseModel()
                                if (exchangeRatelist.isNotEmpty()) {
                                    val currencyValue = getCurrencyValue(exchangeRatelist,amount,source)
                                    convertedList = createConvertedList(exchangeRatelist, currencyValue)
                                }
                                localRepo.insertCurrencyRates(exchangeRatelist)
                                emit(DataState.Success(convertedList))
                            } else {
                                emit(DataState.Error<List<CurrencyRatesEntity>>(response.error))
                            }
                        }
                        is DataState.Error -> {
                            emit(DataState.Error<List<CurrencyRatesEntity>>(response.error))
                        }
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
