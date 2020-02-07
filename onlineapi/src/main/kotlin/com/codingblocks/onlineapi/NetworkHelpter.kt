package com.codingblocks.onlineapi

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

suspend fun <T> safeApiCall(dispatcher: CoroutineDispatcher = Dispatchers.IO, apiCall: suspend () -> T): ResultWrapper<T> {
    return withContext(dispatcher) {
        try {
            ResultWrapper.Success(apiCall.invoke())
        } catch (throwable: Throwable) {
            when (throwable) {
                is IOException -> ResultWrapper.GenericError(103, "Not Connected To Internet")
                is UnknownHostException -> ResultWrapper.GenericError(101, ErrorStatus.NO_CONNECTION)
                is SocketTimeoutException -> ResultWrapper.GenericError(102, ErrorStatus.TIMEOUT)
                is HttpException -> ResultWrapper.GenericError(throwable.code(), "HttpException")
                else -> {
                    ResultWrapper.GenericError(null, throwable.localizedMessage)
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

@Suppress("UNCHECKED_CAST")
fun getMeta(meta: MutableMap<String, *>?, key: String): Int? {
    val map = meta?.get("pagination") as HashMap<String, Int>
    return map[key]
}

