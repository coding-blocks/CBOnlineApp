package com.codingblocks.onlineandroidapi.api

import com.codingblocks.onlineandroidapi.models.CourseModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface OnlineApi {

    @GET("courses")
    fun getRecommendedCourses(
            @Query("exclude") query: String = "ratings",
            @Query("filter[recommended]") recommended: String = "true",
            @Query("filter[unlisted]") unlisted: String = "false",
            @Query("include") include: String = "instructors,runs",
            @Query("sort") sort: String = "difficulty"): Call<CourseModel>

}