package com.codingblocks.cbonlineapp.util

import android.content.Context
import android.net.ConnectivityManager
import com.codingblocks.cbonlineapp.CBOnlineApp
import okhttp3.Cache
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import java.io.File
import java.util.concurrent.TimeUnit

object NetworkUtils {
    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return connectivityManager.activeNetworkInfo != null && connectivityManager.activeNetworkInfo.isConnected
    }

    fun connectedToWifi(context: Context): Boolean? {
        val connManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        val mWifi = connManager?.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        return mWifi?.isConnected
    }

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
