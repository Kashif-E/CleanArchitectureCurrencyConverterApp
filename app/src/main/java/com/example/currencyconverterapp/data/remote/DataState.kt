package com.example.currencyconverterapp.data.remote


/**
 * A generic class that holds a value with its loading status.
 * @param <T>
</T> */
sealed  class DataState<T>(
    val data: T? = null,
    val error : CustomMessages= CustomMessages.SomethingWentWrong("Something Went Wrong")
) {
class Success<T>(data: T?) : DataState<T>(data)
class Loading<T> : DataState<T>()
class Error<T>(customMessages: CustomMessages):DataState<T>(error =  customMessages)
sealed class CustomMessages(val message: String="")  {

    object Timeout : CustomMessages()
    object emptyData: CustomMessages()
    object ServerBusy : CustomMessages()
    object HttpException : CustomMessages()
    object SocketTimeOutException : CustomMessages()
    object NoInternet : CustomMessages()
    object Unauthorized : CustomMessages()
    object InternalServerError : CustomMessages()
    object BadRequest : CustomMessages()
    object Conflict : CustomMessages()
    object NotFound : CustomMessages()
    object NotAcceptable : CustomMessages()
    object ServiceUnavailable : CustomMessages()
    object Forbidden : CustomMessages()
    data class SomethingWentWrong(val error: String) : CustomMessages(message = error)


}

}
