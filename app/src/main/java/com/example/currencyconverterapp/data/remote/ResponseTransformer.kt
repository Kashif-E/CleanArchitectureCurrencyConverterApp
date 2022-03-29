package com.example.currencyconverterapp.data.remote

/**
 * A suspend function for handling success response.
 */
@SuspensionFunction
suspend fun <T> ApiResponse<T>.onSuccessSuspend(
    onResult: suspend DataState.Success<T>.() -> Unit,
    onResultNull: suspend DataState.Error<T>.() -> Unit
): ApiResponse<T> {
    if (this is ApiResponse.ApiSuccessResponse) {
        this.data?.let {
            onResult(DataState.Success(this.data))
        } ?: run {
            onResultNull(
                DataState.Error(handleException<Int>(response.code(), "Something went wrong."))
            )
        }

    }
    return this
}

/**
 * A suspend function for handling error response.
 */
@SuspensionFunction
suspend fun <T> ApiResponse<T>.onErrorSuspend(
    onResult: suspend ApiResponse.ApiFailureResponse.Error<T>.() -> Unit
): ApiResponse<T> {
    if (this is ApiResponse.ApiFailureResponse.Error) {
        onResult(this)
    }
    return this
}

/**
 * A suspend function for handling exception response.
 */
@SuspensionFunction
suspend fun <T> ApiResponse<T>.onExceptionSuspend(
    onResult: suspend ApiResponse.ApiFailureResponse.Exception<T>.() -> Unit
): ApiResponse<T> {
    if (this is ApiResponse.ApiFailureResponse.Exception) {
        onResult(this)
    }
    return this
}

/** A message from the [ApiResponse.ApiFailureResponse.Error]. */
fun <T> ApiResponse.ApiFailureResponse.Error<T>.error(): DataState.Error<T> = DataState.Error(error)

/** A message from the [ApiResponse.ApiFailureResponse.Exception]. */
fun <T> ApiResponse.ApiFailureResponse.Exception<T>.error(): DataState.Error<T> =
    DataState.Error(error)
