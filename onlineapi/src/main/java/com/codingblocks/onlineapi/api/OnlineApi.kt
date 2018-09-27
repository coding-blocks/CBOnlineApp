package com.codingblocks.onlineapi.api

import com.codingblocks.onlineapi.models.CourseModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface OnlineApi {

    interface JsonPlaceholderApi {

        @GET("courses")
        fun getRecommendedCourses(
                @Query("exclude") query: String = "ratings",
                @Query("filter[recommended]") recommended: String = "true",
                @Query("filter[unlisted]") unlisted: String = "false",
                @Query("include") include: String = "instructors,runs",
                @Query("sort") sort: String = "difficulty"): Call<CourseModel>
    }

}