package com.codingblocks.onlineapi.api

import com.codingblocks.onlineapi.models.CodeChallenge
import com.codingblocks.onlineapi.models.ContentProgress
import com.codingblocks.onlineapi.models.DoubtNetworkModel
import com.codingblocks.onlineapi.models.Doubts
import com.codingblocks.onlineapi.models.Note
import retrofit2.Response
import retrofit2.http.*

interface ContentApiService {
    @DELETE("notes/{noteid}")
    suspend fun deleteNoteById(
        @Path("noteid") id: String
    ): Response<Note>

    @PATCH("notes/{noteid}")
    suspend fun updateNoteById(
        @Path("noteid") id: String,
        @Body params: Note
    ): Response<Note>

    @POST("notes")
    suspend fun createNote(
        @Body params: Note
    ): Response<Note>

    @POST("progresses")
    suspend fun setProgress(
        @Body params: ContentProgress
    ): Response<ContentProgress>

    @PATCH("progresses/{id}")
    suspend fun updateProgress(
        @Path("id") id: String,
        @Body params: ContentProgress
    ): Response<ContentProgress>


    @GET("code_challenges/{codeId}/content")
    suspend fun getCodeChallenge(
        @Path("codeId") codeId: Int,
        @Query("contest_id") include: String = ""
    ): Response<CodeChallenge>

    @GET("notes")
    suspend fun getNotesForContent(
        @Query("filter[runAttemptId]") attemptId: String,
        @Query("filter[contentId]") contentId: String
    ): Response<List<Note>>

    @GET("doubts")
    suspend fun getDoubtsForContent(
        @Query("filter[runAttemptId]") attemptId: String,
        @Query("filter[contentId]") contentId: String
    ): Response<List<DoubtNetworkModel>>
}
