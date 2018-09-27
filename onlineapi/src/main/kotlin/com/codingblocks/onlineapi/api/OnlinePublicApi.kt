package com.codingblocks.onlineapi.api

import com.codingblocks.onlineapi.models.Course
import com.codingblocks.onlineapi.models.Instructor
import retrofit2.Call
import retrofit2.http.GET

interface OnlinePublicApi {
    @get:GET("courses")
    val courses: Call<ArrayList<Course>>

    @get:GET("instructors")
    val instructors: Call<ArrayList<Instructor>>
}