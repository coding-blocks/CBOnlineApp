package com.codingblocks.onlineapi.api

import com.codingblocks.onlineapi.models.*
import kotlinx.coroutines.Deferred
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface OnlineJsonApi {


    @get:GET("courses")
    val courses: Call<ArrayList<Course>>

    @GET("courses/{id}")
    fun courseById(
            @Path("id") id: String
    ): Call<Course>

    @GET("instructors")
    fun instructors(
            @Query("include") include: Array<String>? = null
    ): Call<ArrayList<Instructor>>

    @GET("instructors/{id}")
    fun instructorsById(@Path("id") id: String): Call<Instructor>

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

    @GET("sections/{id}")
    fun getSections(@Path("id") id: String,
                    @Query("exclude") query: String = "contents.*",
                    @Query("include") include: String = "contents",
                    @Query("sort") sort: String = "content.section_content.order"): Deferred<Response<Sections>>


    @GET("runs")
    fun getMyCourses(
            @Query("enrolled") enrolled: String = "true",
            @Query("include") include: String = "course,run_attempts"): Call<ArrayList<MyCourseRuns>>

    @GET("run_attempts/{runid}")
    fun enrolledCourseById(
            @Path("runid") id: String): Call<MyRunAttempt>

    @GET("quizzes/{quizid}")
    fun getQuizById(
            @Path("quizid") id: String): Call<Quizzes>

    @GET("questions/{questionid}")
    fun getQuestionById(
            @Path("questionid") id: String,
            @Query("include") include: String = "choices"): Call<Question>

    @GET("quiz_attempts")
    fun getQuizAttempt(
            @Query("filter[qnaId]") qnaId: String,
            @Query("sort") sort: String = "-createdAt"): Call<List<QuizAttempt>>

    @POST("progresses")
    fun setProgress(@Body params: Progress): Call<ContentProgress>


    @GET("quiz_attempts/{id}")
    fun getQuizAttemptById(
            @Path("id") id: String): Call<QuizAttempt>

    @POST("quiz_attempts/{id}/submit")
    fun sumbitQuizById(
            @Path("id") id: String): Call<QuizAttempt>

    @POST("quiz_attempts")
    fun createQuizAttempt(@Body params: QuizAttempt): Call<QuizAttempt>

    @PATCH("quiz_attempts/{id}")
    fun updateQuizAttempt(@Path("id") attemptId: String,
                          @Body params: QuizAttempt): Call<QuizAttempt>

    @PATCH("progresses/{id}")
    fun updateProgress(@Path("id") id: String, @Body params: Progress): Call<ContentProgress>


}
