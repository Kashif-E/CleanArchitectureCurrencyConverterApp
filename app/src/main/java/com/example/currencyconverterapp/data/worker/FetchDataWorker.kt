package com.example.currencyconverterapp.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.currencyconverterapp.data.local.models.CurrencyNamesEntity
import com.example.currencyconverterapp.data.local.models.CurrencyRatesEntity
import com.example.currencyconverterapp.data.local.models.asEntity
import com.example.currencyconverterapp.data.local.repository.LocalRepository
import com.example.currencyconverterapp.data.model.toDataBaseModel

import com.example.currencyconverterapp.data.remote.DataState
import com.example.currencyconverterapp.data.repository.Repository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.withContext

@HiltWorker
class FetchDataWorker @AssistedInject constructor(
    private val localRepository: LocalRepository,
    private val repository: Repository,
    @Assisted val context: Context,
    @Assisted workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        var result = Result.failure()

        withContext(Dispatchers.IO) {
            repository.getCurrencies()
                .zip(repository.getExchangeRates()) { getCurrenciesResponse, getExchangeRatesResponse ->
                    when (getCurrenciesResponse) {
                        is DataState.Success -> {
                            result = if (getCurrenciesResponse.data != null) {
                                saveCurrencyNamesIntoDatabase(getCurrenciesResponse.data.asEntity())
                                Result.success()
                            } else {
                                Result.failure()
                            }
                        }
                        is DataState.Error -> {
                            result = Result.failure()
                        }
                    }

                    when (getExchangeRatesResponse) {
                        is DataState.Success -> {
                            result = if (getExchangeRatesResponse.data != null) {
                                saveCurrencyRatesIntoDatabase(getExchangeRatesResponse.data)
                                Result.success()
                            } else {
                                Result.failure()
                            }
                        }
                        is DataState.Error -> {
                            result = Result.failure()
                        }
                    }
                }.collect {
                    result = Result.success()
                }
        }
        return result
    }

    private fun saveCurrencyNamesIntoDatabase(currencyNameListEntity: List<CurrencyNamesEntity>) {
        localRepository.insertCurrencyNames(currencyNameListEntity)
    }

    private fun saveCurrencyRatesIntoDatabase(currencyRatesEntityList: List<CurrencyRatesEntity>) {
        localRepository.insertCurrencyRates(currencyRatesEntityList)
    }
}