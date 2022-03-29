package com.example.currencyconverterapp.utils

object Constants {

    //End Points
    const val LIVE_END_POINT = "/live"
    const val CURRENCIES_END_POINT = "list"
    const val TIME_OUT = 1500L

    //Other constants
    const val DEFAULT_SOURCE_CURRENCY = "USD"
    const val REMOVE_SOURCE_STRING_FOR_USD = "USDUSD"
    const val WORKER_TAG = "FetchDataWorker"
    const val DATABASE_NAME = "myDataBase.db"
    const val TABLE_CURRENCY = "currencies"
    const val TABLE_CURRENCY_RATES = "currency_rates"
}