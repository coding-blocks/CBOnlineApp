package com.codingblocks.onlineapi.api

import com.codingblocks.onlineapi.models.Course
import com.codingblocks.onlineapi.models.Instructor
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface OnlinePublicApi {

    @GET("courses")
    fun courses (
            @Query("include") include: Array<String>? = null
    ): Call<ArrayList<Course>>

    @get:GET("courses")
    val courses : Call<ArrayList<Course>>

    @GET("instructors")
    fun instructors(
            @Query("include") include: Array<String>? = null
    ): Call<ArrayList<Instructor>>

    @get:GET("instructors")
    val instructors: Call<ArrayList<Instructor>>
}