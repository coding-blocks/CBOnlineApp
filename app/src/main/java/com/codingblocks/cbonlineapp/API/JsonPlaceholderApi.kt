package com.codingblocks.cbonlineapp.API

import com.codingblocks.cbonlineapp.RatingModel
import com.codingblocks.cbonlineapp.User
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface JsonPlaceholderApi {
    @GET("me")
    fun getMe(@Header("Authorization") authorization: String): Call<User>

    @GET("courses/{id}/rating")
    fun getCourseRating(@Path("id") id: String): Call<RatingModel>
}
