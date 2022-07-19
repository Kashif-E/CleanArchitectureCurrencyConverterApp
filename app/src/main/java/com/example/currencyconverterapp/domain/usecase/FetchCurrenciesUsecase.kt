package com.example.currencyconverterapp.domain.usecase

import com.example.currencyconverterapp.data.repository.Repository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

class FetchCurrenciesUsecase @Inject constructor(
    private val repository: Repository
) {

    @ExperimentalCoroutinesApi
    suspend operator fun invoke() = repository.getCurrencies()

}
