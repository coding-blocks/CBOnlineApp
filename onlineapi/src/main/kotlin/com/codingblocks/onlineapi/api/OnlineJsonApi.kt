package com.codingblocks.onlineapi.api

import com.apple.eawt.Application
import com.codingblocks.onlineapi.models.*
import kotlinx.coroutines.Deferred
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.*

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
    fun getRecommendedCourses(
        @Query("exclude") query: String = "ratings,instructors.*,feedbacks,runs.*",
        @Query("filter[recommended]") recommended: String = "true",
        @Query("filter[unlisted]") unlisted: String = "false",
        @Query("page[limit]") page: String = "12",
        @Query("include") include: String = "instructors,runs",
        @Query("sort") sort: String = "difficulty"
    ): Call<ArrayList<Course>>

    @GET("courses")
    fun getAllCourses(
        @Query("exclude") query: String = "ratings,instructors.*",
        @Query("filter[unlisted]") unlisted: String = "false",
        @Query("include") include: String = "instructors,runs",
        @Query("page[limit]") page: String = "8",
        @Query("page[offset]") offset: String = "0",
        @Query("sort") sort: String = "difficulty"
    ): Call<ArrayList<Course>>

    @GET("sections/{id}")
    fun getSections(
        @Path("id") id: String,
        @Query("exclude") query: String = "contents.*",
        @Query("include") include: String = "contents",
        @Query("sort") sort: String = "content.section_content.order"
    ): Deferred<Response<Sections>>


    @GET("runs")
    fun getMyCourses(
        @Query("enrolled") enrolled: String = "true",
        @Query("include") include: String = "course,run_attempts"
    ): Call<ArrayList<MyCourseRuns>>

    @GET("run_attempts/{runid}")
    fun enrolledCourseById(
        @Path("runid") id: String
    ): Call<MyRunAttempt>

    @GET("sections/{sectionid}/relationships/contents")
    fun getSectionContent(
        @Path("sectionid") id: String
    ): Call<ArrayList<LectureContent>>

    @GET("{sectionlink}")
    fun getSectionContents(
        @Path("sectionlink") sectionlink: String
    ): Call<ArrayList<LectureContent>>

    @GET("quizzes/{quizid}")
    fun getQuizById(
        @Path("quizid") id: String
    ): Call<Quizzes>

    @GET("questions/{questionid}")
    fun getQuestionById(
        @Path("questionid") id: String,
        @Query("include") include: String = "choices"
    ): Call<Question>

    @GET("quiz_attempts")
    fun getQuizAttempt(
        @Query("filter[qnaId]") qnaId: String,
        @Query("sort") sort: String = "-createdAt"
    ): Call<List<QuizAttempt>>

    @POST("progresses")
    fun setProgress(@Body params: Progress): Call<ContentProgress>


    @GET("quiz_attempts/{id}")
    fun getQuizAttemptById(
        @Path("id") id: String
    ): Call<QuizAttempt>

    @POST("quiz_attempts/{id}/submit")
    fun sumbitQuizById(
        @Path("id") id: String
    ): Call<QuizAttempt>

    @POST("doubts")
    fun createDoubt(@Body params: DoubtsJsonApi): Call<DoubtsJsonApi>

    @PATCH("doubts/{doubtid}")
    fun resolveDoubt(@Path("doubtid") id: String, @Body params: DoubtsJsonApi): Call<DoubtsJsonApi>

    @POST("comments")
    fun createComment(@Body params: Comment): Call<Comment>

    @GET("doubts/{comentid}/relationships/comments")
    fun getCommentsById(@Path("comentid") id: String): Call<List<Comment>>

    @GET("run_attempts/{runAttemptId}/relationships/doubts")
    fun getDoubtByAttemptId(@Path("runAttemptId") id: String): Call<ArrayList<DoubtsJsonApi>>

    @GET("run_attempts/{runAttemptId}/relationships/notes")
    fun getNotesByAttemptId(@Path("runAttemptId") id: String): Call<ArrayList<Note>>

    @DELETE("notes/{noteid}")
    fun deleteNoteById(@Path("noteid") id: String): Call<Note>

    @PATCH("notes/{noteid}")
    fun updateNoteById(@Path("noteid") id: String, @Body params: Notes): Call<ResponseBody>

    @POST("notes")
    fun createNote(@Body params: Notes): Call<Notes>

    @POST("quiz_attempts")
    fun createQuizAttempt(@Body params: QuizAttempt): Call<QuizAttempt>

    @PATCH("quiz_attempts/{id}")
    fun updateQuizAttempt(
        @Path("id") attemptId: String,
        @Body params: QuizAttempt
    ): Call<QuizAttempt>


    @PATCH("progresses/{id}")
    fun updateProgress(@Path("id") id: String, @Body params: Progress): Call<ContentProgress>

    @get:GET("carousel_cards?sort=order")
    val carouselCards: Call<ArrayList<CarouselCards>>

    @POST("players")
    fun setPlayerId(@Body params: Player): Call<ResponseBody>

    @GET("cricket_cup/user_predictions")
    fun getUserPrediction(
        @Query("filter[cricketCupMatchId]") matchId: String
    ): Call<ArrayList<UserPrediction>>

    @POST("cricket_cup/user_predictions")
    fun setUserPrediction(@Body params: UserPredictionPost): Call<ArrayList<UserPrediction>>

    @GET("cricket_cup/matches/today")
    fun getMatch(): Call<ArrayList<Match>>


    @GET("jobs")
    fun getJobs(
        @Query("filter[deadline][\$gt]") deadline: String,
        @Query("filter[postedOn][\$lte]") postedOn: String,
        @Query("page[offset]") pageOffSet: String = "0",
        @Query("page[limit]") pageLimit: String = "12",
        @Query("sort") sort: String = "-postedOn"
    ): Call<ArrayList<Jobs>>

    @GET("companies/{id}")
    fun getCompany(
        @Path("id") id: String
    ): Call<Company>

    @GET("jobs/{id}")
    fun getJobById(
        @Path("id") id: String
    ): Call<Jobs>

    @POST("applications")
    fun applyJob(@Body params: Applications): Call<ResponseBody>


}
