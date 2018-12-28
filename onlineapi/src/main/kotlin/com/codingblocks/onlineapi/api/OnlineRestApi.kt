package com.codingblocks.onlineapi.api

import com.codingblocks.onlineapi.models.RatingModel
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.*

interface OnlineRestApi {
    @GET("v2/run_attempts/{runid}/progress")
    fun getMyCourseProgress(@Path("runid") id: String): Call<HashMap<Any, Any>>

    /*
    Hit this endpoint to get the API KEY for downloading video and the m3u8 files
    The videoUrl param contains the URL of the file that you're trying to download.
    In case you get a 403, hit this endpoint again and fetch a fresh api

    response :
    {
        policyString : "",
        signature : "",
        keyId : ""
    }

    Send this response as a query param to the API endpoint that lets you download the video
     */
    @GET("v2/aws/cookie")
    fun getVideoDownloadKey(@Query("url") videoUrl: String): Call<JsonObject>

    @POST("jwt/login?android=true")
    @FormUrlEncoded
    fun getToken(@Field("code") code: String): Call<JsonObject>

    @GET("v2/courses/{id}/rating")
    fun getCourseRating(@Path("id") id: String): Call<RatingModel>

    @GET("v2/users/me")
    fun getMe(): Call<JsonObject>

    @GET("v2/runs/{runId}/enroll")
    fun enrollTrial(@Path("runId") id: String): Call<JsonObject>

    @GET("v2/runs/{runId}/buy")
    fun addToCart(@Path("runId") id: String): Call<JsonObject>
}