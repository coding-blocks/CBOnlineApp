package com.codingblocks.onlineapi

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

suspend fun <T> safeApiCall(dispatcher: CoroutineDispatcher, apiCall: suspend () -> T): ResultWrapper<T> {
    return withContext(dispatcher) {
        try {
            ResultWrapper.Success(apiCall.invoke())
        } catch (throwable: Throwable) {
            when (throwable) {
                is UnknownHostException -> ResultWrapper.GenericError(101, ErrorStatus.NO_CONNECTION)
                is SocketTimeoutException -> ResultWrapper.GenericError(102, ErrorStatus.TIMEOUT)
                is IOException -> ResultWrapper.GenericError(103, "IOException")
                is HttpException -> ResultWrapper.GenericError(throwable.code(), "HttpException")
                else -> {
                    ResultWrapper.GenericError(null, ErrorStatus.NOT_DEFINED)
                }
            }
        }
    }
}

fun fetchError(code: Int): String {
    if (code in 401..403) {
        return ErrorStatus.UNAUTHORIZED
    }
    return ErrorStatus.NOT_DEFINED
}

