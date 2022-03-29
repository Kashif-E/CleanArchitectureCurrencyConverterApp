package com.example.currencyconverterapp.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.currencyconverterapp.data.remote.DataState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

open class BaseViewModel: ViewModel() {

    val responseMessage : MutableSharedFlow<DataState.CustomMessages> = MutableSharedFlow()

    val loading : MutableStateFlow<Boolean> = MutableStateFlow(true)


    protected fun showLoader(){
        viewModelScope.launch {
            loading.emit(true)
        }
    }

    protected fun hideLoading(){
        viewModelScope.launch {
            loading.emit(false)
        }
    }

    protected fun onResponseComplete(message : DataState.CustomMessages){
        viewModelScope.launch {
            responseMessage.emit(message)
        }
    }
}