package com.example.currencyconverterapp.data.repository

import androidx.annotation.WorkerThread
import com.example.currencyconverterapp.data.local.models.CurrencyRatesEntity
import com.example.currencyconverterapp.data.local.models.asDomainModel
import com.example.currencyconverterapp.data.local.repository.LocalRepository
import com.example.currencyconverterapp.data.model.asEntities
import com.example.currencyconverterapp.data.model.toDataBaseModel
import com.example.currencyconverterapp.data.remote.*
import com.example.currencyconverterapp.domain.domain_models.CurrencyNameDomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * This is an implementation of [Repository] to handle communication with [ApiService] server.
 */
class RepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val localRepo: LocalRepository
) : Repository {

    @WorkerThread
    override suspend fun getCurrencies(): Flow<DataState<List<CurrencyNameDomainModel>>> = flow {

        val responseFromLocalDatabase = localRepo.getAllCurrencyNames()
        if (responseFromLocalDatabase.isNotEmpty()) {
            emit(DataState.Success(responseFromLocalDatabase.asDomainModel()))
        } else {

            apiService.getCurrencies().apply {
                this.onSuccessSuspend(onResult = {
                   if (this.data?.success == true){
                       val data = this.data.asEntities()
                       localRepo.insertCurrencyNames(data)
                       emit(DataState.Success(data.asDomainModel()))
                   }else{

                       emit(error("No access key."))
                   }



                }, onResultNull = {


                    emit(error(this.error))

                }).onErrorSuspend {
                    emit(error(this.error))
                }.onExceptionSuspend {
                    emit(error(this.error))
                }
            }
        }
    }


        override suspend fun getExchangeRates(): Flow<DataState<List<CurrencyRatesEntity>>> =
            flow {
                val responseFromDatabase = localRepo.getAllCurrencyRates()
                if (responseFromDatabase.isEmpty().not()) {

                    emit(DataState.Success(responseFromDatabase))
                } else {
                    apiService.getExchangeRates().apply {
                        this.onSuccessSuspend({

                            val exchangeRatelist: List<CurrencyRatesEntity>
                            if (this.data!!.success) {
                                exchangeRatelist = this.data.toDataBaseModel()

                                localRepo.insertCurrencyRates(exchangeRatelist)
                                emit(DataState.Success(exchangeRatelist))
                            }
                        }, {
                            emit(error("Something went wrong."))
                        }).onErrorSuspend {
                            emit(error(this.error))
                        }.onExceptionSuspend {
                            emit(error(this.error))
                        }
                    }
                }
            }


    }

