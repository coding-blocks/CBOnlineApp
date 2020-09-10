package com.codingblocks.onlineapi.api

import com.codingblocks.onlineapi.models.Question
import com.codingblocks.onlineapi.models.QuizAttempt
import com.codingblocks.onlineapi.models.Quizzes
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface QuizApiService {

    @GET("quizzes/{quizId}")
    suspend fun getQuizById(
        @Path("quizId") id: String
    ): Response<Quizzes>

    @GET("quiz_attempts")
    suspend fun getQuizAttempt(
        @Query("filter[qnaId]") qnaId: String,
        @Query("sort") sort: String = "-createdAt"
    ): Response<List<QuizAttempt>>

    @POST("quiz_attempts")
    suspend fun createQuizAttempt(
        @Body params: QuizAttempt
    ): Response<QuizAttempt>

    @PATCH("quiz_attempts/{id}")
    fun updateQuizAttempt(
        @Path("id") attemptId: String,
        @Body params: QuizAttempt
    ): Call<QuizAttempt>

    @GET("questions/{questionId}")
    fun getQuestionById(
        @Path("questionId") id: String,
        @Query("include") include: String = "choices"
    ): Call<Question>

    @GET("quiz_attempts/{id}")
    suspend fun getQuizAttemptById(
        @Path("id") id: String
    ): Response<QuizAttempt>

    @POST("quiz_attempts/{id}/submit")
    suspend fun submitQuizById(
        @Path("id") id: String
    ): Response<QuizAttempt>
}
