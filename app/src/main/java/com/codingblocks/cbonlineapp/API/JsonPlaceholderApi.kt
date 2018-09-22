package com.codingblocks.cbonlineapp.API

import com.codingblocks.cbonlineapp.CourseModel
import com.codingblocks.cbonlineapp.SingleCourse
import com.codingblocks.cbonlineapp.User
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface JsonPlaceholderApi {
    @GET("me")
    fun getMe(@Header("Authorization") authorization: String): Call<User>

    @GET("{id}")
    fun getUser(@Header("Authorization") authorization: String, @Path("id") id: Int): Call<User>

    @GET("courses")
    fun getRecommendedCourses(@Query("exclude") query: String = "ratings",
                              @Query("filter[recommended]") recommended: String = "true",
                              @Query("filter[unlisted]") unlisted: String = "false",
                              @Query("include") include: String = "instructors,runs",
                              @Query("sort") sort: String = "difficulty"): Call<CourseModel>

    @GET("courses/complete-java-course-online")
    fun getCourse(): Call<SingleCourse>
}
