package com.codingblocks.onlineapi.api

import com.codingblocks.onlineapi.models.DoubtLeaderBoard
import com.codingblocks.onlineapi.models.DoubtNetworkModel
import com.codingblocks.onlineapi.models.Doubts
import com.github.jasminb.jsonapi.JSONAPIDocument
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path
import retrofit2.http.Query

interface AdminApiService {

    @GET("doubts")
    suspend fun getLiveDoubts(
        @Query("exclude") query: String = "content.*",
        @Query("filter[status]") filter: String = "PENDING",
        @Query("include") include: String = "content",
        @Query("page[limit]") page: String = "10",
        @Query("page[offset]") offset: Int = 0,
        @Query("sort") sort: String = "-createdAt"
    ): Response<JSONAPIDocument<List<DoubtNetworkModel>>>

    @GET("doubts")
    suspend fun getMyDoubts(
        @Query("exclude") query: String = "content.*",
        @Query("filter[acknowledgedById]") acknowledgedId: String,
        @Query("filter[status]") filter: String = "ACKNOWLEDGED",
        @Query("include") include: String = "content",
        @Query("page[limit]") page: String = "10",
        @Query("page[offset]") offset: String = "0",
        @Query("sort") sort: String = "-acknowledgedAt"
    ): Response<JSONAPIDocument<List<DoubtNetworkModel>>>

    @GET("doubt_leaderboards")
    suspend fun getLeaderBoard(
        @Query("filter[visible_all]") filter: String = "true",
        @Query("include") include: String = "user",
        @Query("sort") sort: String = "-rating_all",
        @Query("page[limit]") page: String = "10",
        @Query("page[offset]") offset: Int = 0
    ): Response<JSONAPIDocument<List<DoubtLeaderBoard>>>

    @PATCH("doubts/{id}")
    suspend fun acknowledgeDoubt(
        @Path("id") doubtId: String,
        @Body params: Doubts
    ): Response<List<DoubtNetworkModel>>
}
