package com.example.currencyconverterapp.data.repository

import androidx.annotation.WorkerThread
import com.example.currencyconverterapp.data.remote.*
import com.example.currencyconverterapp.data.model.CurrenciesDTO
import com.example.currencyconverterapp.data.model.ExchangeRatesDTO
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * This is an implementation of [Repository] to handle communication with [ApiService] server.
 */
class RepositoryImpl @Inject constructor(
    private val apiService: ApiService,
) : Repository {

    @WorkerThread
    override suspend fun getCurrencies() = flow {
        apiService.getCurrencies().apply {
            this.onSuccessSuspend ({

                emit(this)

            },{
                emit(this)
            }).onErrorSuspend {
                emit(error())
            }.onExceptionSuspend {
                emit(error())
            }
        }
    }


    override suspend fun getExchangeRates() =
        flow {
            apiService.getExchangeRates().apply {
                this.onSuccessSuspend ({
                    emit(this)
                },{
                    emit(this)
                }).onErrorSuspend {
                    emit(error())
                }.onExceptionSuspend {
                    emit(error())
                }
            }
        }


}
