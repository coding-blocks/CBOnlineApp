package com.codingblocks.cbonlineapp.API

import com.codingblocks.cbonlineapp.User

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface JsonPlaceholderApi {
    @GET("me")
    fun getMe(@Header("Authorization") authorization: String): Call<User>

    @GET("{id}")
    fun getUser(@Header("Authorization") authorization: String, @Path("id") id: Int): Call<User>
}
