package com.codingblocks.cbonlineapp.util

import com.codingblocks.cbonlineapp.CBOnlineApp
import java.io.File
import java.util.concurrent.TimeUnit
import okhttp3.Cache
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.OkHttpClient

object NetworkUtils {

    val okHttpClient = provideOkHttpClient()

    private fun provideOkHttpClient(): OkHttpClient {
        val cache = Cache(File(CBOnlineApp.mInstance.cacheDir, "http-cache"), 10 * 1024 * 1024)
        return OkHttpClient.Builder()
            .addNetworkInterceptor(provideCacheInterceptor())
            .cache(cache)
            .build()
    }

    private fun provideCacheInterceptor(): Interceptor {
        return Interceptor { chain ->
            val response = chain.proceed(chain.request())
            val cacheControl = CacheControl.Builder()
                .maxAge(7, TimeUnit.DAYS)
                .build()

            response.newBuilder()
                .header("Cache-Control", cacheControl.toString())
                .build()
        }
    }
}
