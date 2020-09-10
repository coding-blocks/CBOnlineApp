package com.codingblocks.onlineapi.api

import com.codingblocks.onlineapi.models.CourseNetworkModel
import com.codingblocks.onlineapi.models.Professions
import com.codingblocks.onlineapi.models.TracksNetworkModel
import retrofit2.Response
import retrofit2.http.*

interface TracksApiService {

    @GET("career_tracks")
    suspend fun getTracks(
        @Query("include") include: String = "professions"
    ): Response<List<TracksNetworkModel>>

    @GET("{id}")
    suspend fun getTrackCourses(
        @Path("id") path: String
    ): Response<List<CourseNetworkModel>>

    @GET("career_tracks/{id}")
    suspend fun getTrack(
        @Path("id") id: String
    ): Response<TracksNetworkModel>

    @GET("professions")
    suspend fun getProfessions(): Response<List<Professions>>

    @POST("career_tracks/recommend")
    @FormUrlEncoded
    suspend fun getRecommendedTrack(
        @FieldMap params: HashMap<String, String>
    ): Response<TracksNetworkModel>
}
