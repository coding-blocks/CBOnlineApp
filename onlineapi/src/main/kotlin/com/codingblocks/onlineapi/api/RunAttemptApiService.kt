package com.codingblocks.onlineapi.api

import com.codingblocks.onlineapi.models.RunAttemptNetworkModel
import com.codingblocks.onlineapi.models.RunAttempts
import com.codingblocks.onlineapi.models.RunNetworkModel
import com.github.jasminb.jsonapi.JSONAPIDocument
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path
import retrofit2.http.Query

interface RunAttemptApiService {

    @GET("runs/lastAccessedRun")
    suspend fun getLastAccessed(
        @Query("include") include: String = "course,run_attempts"
    ): Response<RunNetworkModel>

    @GET("runs")
    suspend fun getMyCourses(
        @Query("enrolled") enrolled: String = "true",
        @Query("page[offset]") offset: String = "0",
        @Query("include") include: String = "course,run_attempts"
    ): Response<JSONAPIDocument<List<RunNetworkModel>>>


    @GET("run_attempts/{id}")
    suspend fun enrolledCourseById(
        @Path("id") id: String
    ): Response<RunAttemptNetworkModel>

    @PATCH("run_attempts/{id}/pause")
    suspend fun pauseCourse(
        @Path("id") id: String
    ): Response<RunAttemptNetworkModel>

    @PATCH("run_attempts/{id}/unpause")
    suspend fun unPauseCourse(
        @Path("id") id: String
    ): Response<RunAttemptNetworkModel>
}
