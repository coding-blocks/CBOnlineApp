package com.codingblocks.onlineapi.api

import com.codingblocks.onlineapi.models.User
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface AuthApi {
    @GET("me")
    fun getMe(@Header("Authorization") authorization: String): Call<User>

    @GET("{id}")
    fun getUser(@Header("Authorization") authorization: String, @Path("id") id: Int): Call<User>
}
