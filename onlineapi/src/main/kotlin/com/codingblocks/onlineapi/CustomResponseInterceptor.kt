package com.codingblocks.onlineapi

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import org.omg.CORBA.Object
import java.io.IOException

class CustomResponseInterceptor : Interceptor {
    private val bodyString: String? = null

    private val TAG = javaClass.simpleName

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response? {
        val request = chain.request()
        val response = chain.proceed(request)
        if (response.code() != 200) {
            var r: Response? = null
            try {
                r = makeTokenRefreshCall(request, chain)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return r
        }

        print(response.toString())
        //        printl.d(TAG, "INTERCEPTED:$ " response.toString());
        return response
    }

    @Throws(Exception::class, IOException::class)
    private fun makeTokenRefreshCall(req: Request, chain: Interceptor.Chain): Response {
        //        Log.d(TAG, "Retrying new request");
        print("Retrying new request")
        /* fetch refreshed token, some synchronous API call, whatever */
        //        String newToken = fetchToken();
        /* make a new request which is same as the original one, except that its headers now contain a refreshed token */
        val newRequest: Request
        newRequest = req.newBuilder().header("Authorization", " Token " + newToken!!).build()
        val another = chain.proceed(newRequest)
        while (another.code() != 200) {
            makeTokenRefreshCall(newRequest, chain)
        }
        return another
    }

    companion object {

        private val newToken: String? = null
    }
}

