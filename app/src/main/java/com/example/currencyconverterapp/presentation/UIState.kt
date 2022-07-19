package com.example.currencyconverterapp.presentation

import com.example.currencyconverterapp.data.remote.DataState

sealed class UIState{
    object LoadingState : UIState()
    object ContentState : UIState()
    object EmptyState : UIState()
    class ErrorState(val message: DataState.CustomMessages) : UIState()
}

