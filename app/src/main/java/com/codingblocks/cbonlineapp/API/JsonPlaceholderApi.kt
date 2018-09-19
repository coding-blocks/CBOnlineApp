package com.codingblocks.cbonlineapp.API

import com.codingblocks.cbonlineapp.CourseModel
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

    @get:GET("courses?exclude=ratings&filter%5Brecommended%5D=true&filter%5Bunlisted%5D=false&include=instructors%2Cruns&sort=difficulty")
    val courseModel: Call<CourseModel>
}
