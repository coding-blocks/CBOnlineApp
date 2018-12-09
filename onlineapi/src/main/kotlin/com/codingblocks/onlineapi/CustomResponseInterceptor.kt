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
        val response = chain.proceed(request)

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
        /* fetch refreshed token, some synchronous API call, whatever */
        val oldUrl = HttpUrl.get(URL(req.url().toString().split("?")[0]))!!
        val newParams = Clients.api.getVideoDownloadKey(oldUrl.toString()).execute().body()   /* make a new request which is same as the original one, except that its headers now contain a refreshed query params */
        val newRequest: Request
        // pass new policy-string as query param for the request
        val url = oldUrl.newBuilder()
                .addQueryParameter("Key-Pair-Id", newParams?.get("keyId")?.asString)
                .addQueryParameter("Signature", newParams?.get("signature")?.asString)
                .addQueryParameter("Policy", newParams?.get("policyString")?.asString)
                .build()
        newRequest = Request.Builder().url(url).build()
        val another = chain.proceed(newRequest)
        while (another.code() == 403) {
            makeTokenRefreshCall(newRequest, chain)
        }
        return another
    }

}

