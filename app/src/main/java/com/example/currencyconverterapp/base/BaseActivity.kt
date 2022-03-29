package com.example.currencyconverterapp.base

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.currencyconverterapp.R
import com.example.currencyconverterapp.data.remote.DataState
import com.google.android.material.snackbar.Snackbar

open class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

     fun showSnackbar(message: DataState.CustomMessages, binding: View) {

        val error= when(message){
            is DataState.CustomMessages.emptyData -> {
                getString(R.string.no_data_found)
            }
            is DataState.CustomMessages.Timeout -> {
                getString(R.string.timeout)
            }
            is DataState.CustomMessages.ServerBusy -> {
                getString(R.string.server_is_busy)
            }

            is DataState.CustomMessages.HttpException -> {
                getString(R.string.no_internet_connection)
            }
            is DataState.CustomMessages.SocketTimeOutException -> {
                getString(R.string.no_internet_connection)
            }
            is DataState.CustomMessages.NoInternet -> {
                getString(R.string.no_internet_connection)
            }
            is DataState.CustomMessages.Unauthorized -> {
                getString(R.string.unauthorized)
            }
            is DataState.CustomMessages.InternalServerError -> {
                getString(R.string.internal_server_error)
            }
            is DataState.CustomMessages.BadRequest -> {
                getString(R.string.bad_request)
            }
            is DataState.CustomMessages.Conflict -> {
                getString(R.string.confirm)
            }
            is DataState.CustomMessages.NotFound -> {
                getString(R.string.not_found)
            }
            is DataState.CustomMessages.NotAcceptable -> {
                getString(R.string.not_acceptable)
            }
            is DataState.CustomMessages.ServiceUnavailable -> {
                getString(R.string.service_unavailable)
            }
            is DataState.CustomMessages.Forbidden -> {
                getString(R.string.forbidden)
            }

            else -> {
                "Something went Wrong."
            }
        }

        Snackbar.make(binding.rootView, error, Snackbar.LENGTH_LONG)
            .setActionTextColor(ContextCompat.getColor(this, R.color.white)).also {
                it.setAction(
                    "OK"
                ) { v ->

                    it.dismiss()
                }
            }
            .show()






    }

}
