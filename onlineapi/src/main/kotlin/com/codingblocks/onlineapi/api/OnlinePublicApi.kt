package com.codingblocks.onlineapi.api

import com.codingblocks.onlineapi.models.Course
import com.codingblocks.onlineapi.models.Instructor
import com.codingblocks.onlineapi.models.Sections
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface OnlinePublicApi {

    @GET("courses")
    fun courses(
            @Query("include") include: Array<String>? = null
    ): Call<ArrayList<Course>>

    @get:GET("courses")
    val courses: Call<ArrayList<Course>>

    @GET("course/{id}")
    fun courseById(
            @Path("id") id: String
    ): Call<Course>

    @GET("instructors")
    fun instructors(
            @Query("include") include: Array<String>? = null
    ): Call<ArrayList<Instructor>>

    @get:GET("instructors")
    val instructors: Call<ArrayList<Instructor>>

    @GET("courses")
    fun getRecommendedCourses(@Query("exclude") query: String = "ratings",
                              @Query("filter[recommended]") recommended: String = "true",
                              @Query("filter[unlisted]") unlisted: String = "false",
                              @Query("include") include: String = "instructors,runs",
                              @Query("sort") sort: String = "difficulty"): Call<ArrayList<Course>>
    @GET("courses")
    fun getAllCourses(@Query("exclude") query: String = "ratings",
                              @Query("filter[unlisted]") unlisted: String = "false",
                              @Query("include") include: String = "instructors,runs",
                              @Query("sort") sort: String = "difficulty"): Call<ArrayList<Course>>

    @get:GET("sections/908/?include=contents&exclude=contents.*&sort=content.section_content.order")
    val section: Call<Sections>
}