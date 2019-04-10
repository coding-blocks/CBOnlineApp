package com.codingblocks.onlineapi

import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.net.URL

class CustomResponseInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response? {
        val request = chain.request()
        val newRequest = Request.Builder().url(getAuthenticalRequestUrl(request.url())).build()
        val response = chain.proceed(newRequest)
        if (response.code() == 403) {
            var r: Response? = null
            try {
                r = makeTokenRefreshCall(request, chain)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return r
        }

        print(response.toString())
        return response
    }

    @Throws(Exception::class, IOException::class)
    private fun makeTokenRefreshCall(req: Request, chain: Interceptor.Chain): Response {
        print("Retrying new request")
        val newRequest: Request = Request.Builder().url(getAuthenticalRequestUrl(req.url())).build()
        val another = chain.proceed(newRequest)
        while (another.code() == 403) {
            makeTokenRefreshCall(newRequest, chain)
        }
        return another
    }

    private fun getAuthenticalRequestUrl(originalUrl: HttpUrl): HttpUrl {
        val oldUrl = HttpUrl.get(URL(originalUrl.toString().split("?")[0]))!!
        val newParams = Clients.api.getVideoDownloadKey(oldUrl.toString()).execute().body()   /* make a new request which is same as the original one, except that its headers now contain a refreshed query params */
        // pass new policy-string as query param for the request
        return oldUrl.newBuilder()
            .addQueryParameter("Key-Pair-Id", newParams?.get("keyId")?.asString)
            .addQueryParameter("Signature", newParams?.get("signature")?.asString)
            .addQueryParameter("Policy", newParams?.get("policyString")?.asString)
            .build()
    }
}

