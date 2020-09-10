package com.codingblocks.onlineapi.api

import com.codingblocks.onlineapi.models.CommentNetworkModel
import com.codingblocks.onlineapi.models.DoubtNetworkModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface DoubtApiService {
    @POST("doubts")
    suspend fun createDoubt(
        @Body params: DoubtNetworkModel
    ): Response<DoubtNetworkModel>

    @POST("comments")
    suspend fun createComment(
        @Body params: CommentNetworkModel
    ): Response<CommentNetworkModel>
}
