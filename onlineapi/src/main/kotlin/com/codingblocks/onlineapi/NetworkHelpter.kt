package com.codingblocks.onlineapi

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import java.net.UnknownHostException

suspend fun <T> safeApiCall(dispatcher: CoroutineDispatcher, apiCall: suspend () -> T): ResultWrapper<T> {
    return withContext(dispatcher) {
        try {
            ResultWrapper.Success(apiCall.invoke())
        } catch (throwable: Throwable) {
            when (throwable) {
                is UnknownHostException -> ResultWrapper.GenericError(1001, ErrorStatus.NO_CONNECTION)
                is IOException -> ResultWrapper.GenericError(101, "IOException")
                is HttpException -> {
                    ResultWrapper.GenericError(throwable.code(), "HttpException")
                }
                else -> {
                    ResultWrapper.GenericError(null, null)
                }
            }
        }
    }
}

