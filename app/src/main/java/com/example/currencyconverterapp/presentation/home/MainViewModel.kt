package com.example.currencyconverterapp.presentation.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.currencyconverterapp.data.local.models.CurrencyRatesEntity
import com.example.currencyconverterapp.data.remote.DataState
import com.example.currencyconverterapp.domain.domain_models.CurrencyNameDomainModel
import com.example.currencyconverterapp.domain.usecase.FetchCurrenciesUsecase
import com.example.currencyconverterapp.domain.usecase.FetchExchangeRatesUsecase

import com.example.currencyconverterapp.presentation.UIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class MainViewModel @Inject constructor(
    private val fetchCurrenciesUsecase: FetchCurrenciesUsecase,
    private val fetchExchangeRatesUsecase: FetchExchangeRatesUsecase
) : ViewModel() {

    private var _uiState = MutableLiveData<UIState>()
    var uiStateLiveData: LiveData<UIState> = _uiState

    private var _currenciesList = MutableLiveData<List<CurrencyNameDomainModel>>()
    var currenciesLiveData: LiveData<List<CurrencyNameDomainModel>> = _currenciesList

    private var _exchangeRateUiState = MutableLiveData<UIState>()
    var exchangeRateUiStateLiveData: LiveData<UIState> = _exchangeRateUiState
    private var _exchangeRatesList = MutableLiveData<List<CurrencyRatesEntity>>()
    var exchangeRatesEntityLiveData: LiveData<List<CurrencyRatesEntity>> = _exchangeRatesList

    init {
        fetchCurrencies()
    }


    private fun fetchCurrencies() {
        _uiState.postValue(UIState.LoadingState)
        viewModelScope.launch(Dispatchers.IO) {
            fetchCurrenciesUsecase.invoke().collect { dataState ->
                withContext(Dispatchers.Main) {
                    when (dataState) {
                        is DataState.Success -> {
                            _uiState.postValue(UIState.ContentState)
                            _currenciesList.postValue(dataState.data ?: emptyList())
                        }
                        is DataState.Error -> {
                            _uiState.postValue(UIState.ErrorState(dataState.error))
                        }

                    }
                }
            }
        }
    }


    fun fetchExchangeRates(source: String, amount: Double) {

        viewModelScope.launch(Dispatchers.IO) {
            fetchExchangeRatesUsecase.invoke(source = source, amount = amount)
                .collect { dataState ->
                    withContext(Dispatchers.Main) {
                        when (dataState) {
                            is DataState.Success -> {
                            //    hideLoading()
                                _exchangeRatesList.postValue(dataState.data!!)
                            }
                            is DataState.Error -> {
                              //  onResponseComplete(dataState.error)
                            }
                        }
                    }
                }
        }
    }
}
