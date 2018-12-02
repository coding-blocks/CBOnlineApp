package com.codingblocks.onlineapi

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
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

        //fetch jwt token from prefs class
        Clients.api.getVideoDownloadKey("JWT " + "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6Mzc5NzUsImZpcnN0bmFtZSI6IlB1bGtpdCIsImxhc3RuYW1lIjoiQWdnYXJ3YWwiLCJ1c2VybmFtZSI6ImFnZ2Fyd2FscHVsa2l0NTk2LWciLCJlbWFpbCI6ImFnZ2Fyd2FscHVsa2l0NTk2QGdtYWlsLmNvbSIsIm1vYmlsZSI6Ijk1ODIwNTQ2NjQiLCJvbmVhdXRoX2lkIjoiMTIwMzUiLCJsYXN0X3JlYWRfbm90aWZpY2F0aW9uIjoiMCIsInBob3RvIjoiaHR0cHM6Ly9ncmFwaC5mYWNlYm9vay5jb20vMTc4MzM4OTczNTAyODQ2MC9waWN0dXJlP3R5cGU9bGFyZ2UiLCJjb2xsZWdlIjoiQW1pdHkgU2Nob29sIE9mIEVuZ2luZWVyaW5nICYgVGVjaG5vbG9neSAoTm9pZGEpIiwicm9sZUlkIjoyLCJjcmVhdGVkQXQiOiIyMDE4LTA5LTI3VDEzOjEwOjU5LjM5NloiLCJ1cGRhdGVkQXQiOiIyMDE4LTEyLTAyVDAzOjU1OjI5LjI5NFoiLCJyb2xlIjp7ImlkIjoyLCJuYW1lIjoiU3R1ZGVudCIsImNyZWF0ZWRBdCI6IjIwMTctMDktMDdUMTA6NTg6MTkuOTkzWiIsInVwZGF0ZWRBdCI6IjIwMTctMDktMDdUMTA6NTg6MTkuOTkzWiJ9LCJjbGllbnRJZCI6IjgyYTA5ODczLWZjN2QtNDQ3ZC05YmQ3LTBlMjdjMjE2YzFmNCIsImlzVG9rZW5Gb3JBZG1pbiI6ZmFsc2UsImlhdCI6MTU0MzcyMjkyOSwiZXhwIjoxNTQzNzI0NDI5fQ.FFt2GjxSy5oVDhmFEgkYPfThJoz6bXOPIWKUSxsPTjewjDhl-8wai_EKlzYcj_BbVwKFtehHS5qxPmu-BB1cJY3zuZ8Kj8PqJP1Eac9Iko4zjjgjzY9bkXBgFFetlqezpSGTJ6fyhSVq9HvDEDCVadg6RBdS6jqzXRHgMULjnwOkMHewED9rPS-KF85cay42j1_mYO05KPV12tSDlvTpDI64B2TZFb5SeKcgntLqIHzZu85jp58GyF1fCUCgy_S7vsfB4JfOrZzGERBTcZWkS5ANE1A-z2d1L82ouu8Zv_CMbV2RL-ZZZnaz-RC5MIi8-ORjnhzHw6JGwuXtXyrf9w", req.url().toString()).execute().isSuccessful    /* make a new request which is same as the original one, except that its headers now contain a refreshed token */
        val newRequest: Request

        // how to pass new policy-string as query param for the request
        newRequest = req.newBuilder().addHeader("Authorization", " Token " + newToken!!).build()
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

