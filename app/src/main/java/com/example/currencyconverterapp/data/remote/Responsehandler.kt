package com.example.currencyconverterapp.data.remote


import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.TimeoutException


enum class ErrorCodes(val code: Int) {
    SocketTimeOut(-1),
    BadRequest(400),
    NotFound(404),
    Conflict(409),
    InternalServerError(500),
    Forbidden(403),
    NotAcceptable(406),
    ServiceUnavailable(503),
    UnAuthorized(401),
}



fun <T : Any> handleException(throwable: Exception): DataState.CustomMessages {


    return when (throwable) {


        is HttpException -> DataState.CustomMessages.HttpException
        is TimeoutException -> DataState.CustomMessages.Timeout
        is ConnectivityInterceptor.NoNetworkException -> DataState.CustomMessages.NoInternet
        is UnknownHostException -> DataState.CustomMessages.ServerBusy
        is ConnectException -> DataState.CustomMessages.NoInternet
        is SocketTimeoutException -> DataState.CustomMessages.SocketTimeOutException
        else -> DataState.CustomMessages.NoInternet
    }
}


fun <T : Any> handleException(statusCode: Int, message: String): DataState.CustomMessages {
    return getErrorType(statusCode, message)
}

private fun getErrorType(code: Int, message: String): DataState.CustomMessages {
    return when (code) {
        ErrorCodes.SocketTimeOut.code -> DataState.CustomMessages.Timeout
        ErrorCodes.UnAuthorized.code -> DataState.CustomMessages.Unauthorized
        ErrorCodes.InternalServerError.code -> DataState.CustomMessages.InternalServerError

        ErrorCodes.BadRequest.code -> DataState.CustomMessages.BadRequest
        ErrorCodes.Conflict.code -> DataState.CustomMessages.Conflict
        ErrorCodes.InternalServerError.code -> DataState.CustomMessages.InternalServerError

        ErrorCodes.NotFound.code -> DataState.CustomMessages.NotFound
        ErrorCodes.NotAcceptable.code -> DataState.CustomMessages.NotAcceptable
        ErrorCodes.ServiceUnavailable.code -> DataState.CustomMessages.ServiceUnavailable
        ErrorCodes.Forbidden.code -> DataState.CustomMessages.Forbidden
        else -> DataState.CustomMessages.SomethingWentWrong(message)
    }
}
