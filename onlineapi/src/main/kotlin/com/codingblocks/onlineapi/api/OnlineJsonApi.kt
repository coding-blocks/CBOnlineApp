package com.codingblocks.onlineapi.api

import com.codingblocks.onlineapi.models.Applications
import com.codingblocks.onlineapi.models.CarouselCards
import com.codingblocks.onlineapi.models.Comment
import com.codingblocks.onlineapi.models.Company
import com.codingblocks.onlineapi.models.Course
import com.codingblocks.onlineapi.models.DoubtLeaderBoard
import com.codingblocks.onlineapi.models.Doubts
import com.codingblocks.onlineapi.models.Instructor
import com.codingblocks.onlineapi.models.Jobs
import com.codingblocks.onlineapi.models.LectureContent
import com.codingblocks.onlineapi.models.Note
import com.codingblocks.onlineapi.models.Notes
import com.codingblocks.onlineapi.models.Player
import com.codingblocks.onlineapi.models.Progress
import com.codingblocks.onlineapi.models.Project
import com.codingblocks.onlineapi.models.Question
import com.codingblocks.onlineapi.models.QuizAttempt
import com.codingblocks.onlineapi.models.Quizzes
import com.codingblocks.onlineapi.models.RunAttempts
import com.codingblocks.onlineapi.models.Runs
import com.codingblocks.onlineapi.models.Sections
import com.codingblocks.onlineapi.models.User
import com.github.jasminb.jsonapi.JSONAPIDocument
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

interface OnlineJsonApi {



    @GET("courses/{id}")
    suspend fun getCourse(
        @Path("id") id: String
    ): Response<Course>

    @GET("projects/{id}")
    suspend fun getProject(
        @Path("id") id: String,
        @Query("exclude") query: String = "course.*"
    ): Response<Project>

    @GET("sections/{id}")
    suspend fun getSections(
        @Path("id") id: String,
        @Query("exclude") query: String = "contents.*",
        @Query("include") include: String = "contents",
        @Query("sort") sort: String = "content.section_content.order"
    ): Response<Sections>

    @GET("run_attempts/{runAttemptId}/relationships/doubts")
    suspend fun getDoubtByAttemptId(
        @Path("runAttemptId") id: String
    ): Response<List<Doubts>>

    @PATCH("doubts/{doubtid}")
    suspend fun resolveDoubt(
        @Path("doubtid") id: String,
        @Body params: Doubts
    ): Response<Doubts>

    @PATCH("doubts/{doubtId}")
    suspend fun getDoubt(
        @Path("doubtId") id: String
    ): Response<Doubts>

    @GET("doubts/{doubtId}/relationships/comments")
    suspend fun getCommentsById(
        @Path("doubtId") id: String
    ): Response<List<Comment>>

    @GET("runs")
    suspend fun getMyCourses(
        @Query("enrolled") enrolled: String = "true",
        @Query("page[offset]") offset: String = "0",
        @Query("include") include: String = "course,run_attempts"
    ): Response<JSONAPIDocument<List<Runs>>>


    @GET("instructors/{id}")
    suspend fun instructorsById(@Path("id") id: String): Response<Instructor>

    @GET("courses")
    suspend fun getRecommendedCourses(
        @Query("exclude") query: String = "ratings,instructors.*,feedbacks,runs.*",
        @Query("filter[recommended]") recommended: String = "true",
        @Query("filter[unlisted]") unlisted: String = "false",
        @Query("page[limit]") page: String = "12",
        @Query("include") include: String = "instructors,runs",
        @Query("sort") sort: String = "difficulty"
    ): Response<List<Course>>

    @GET("run_attempts/{runid}")
    suspend fun enrolledCourseById(
        @Path("runid") id: String
    ): Response<RunAttempts>

    @GET("{sectionlink}")
    suspend fun getSectionContents(
        @Path("sectionlink") sectionlink: String
    ): Response<ArrayList<LectureContent>>

    @GET("run_attempts/{runAttemptId}/relationships/notes")
    suspend fun getNotesByAttemptId(
        @Path("runAttemptId") id: String
    ): Response<List<Note>>

    @DELETE("notes/{noteid}")
    suspend fun deleteNoteById(
        @Path("noteid") id: String
    ): Response<Note>



    @GET("courses")
    fun getAllCourses(
        @Query("exclude") query: String = "ratings,instructors.*",
        @Query("filter[unlisted]") unlisted: String = "false",
        @Query("include") include: String = "instructors,runs",
        @Query("page[limit]") page: String = "8",
        @Query("page[offset]") offset: String = "0",
        @Query("sort") sort: String = "difficulty"
    ): Call<ArrayList<Course>>



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
    fun setProgress(@Body params: Progress): Call<Progress>

    @GET("quiz_attempts/{id}")
    fun getQuizAttemptById(
        @Path("id") id: String
    ): Call<QuizAttempt>

    @POST("quiz_attempts/{id}/submit")
    fun sumbitQuizById(
        @Path("id") id: String
    ): Call<QuizAttempt>

    @POST("doubts")
    fun createDoubt(@Body params: Doubts): Call<Doubts>

    @POST("comments")
    fun createComment(@Body params: Comment): Call<Comment>


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
    fun updateProgress(@Path("id") id: String, @Body params: Progress): Call<Progress>

    @get:GET("carousel_cards?sort=order")
    val carouselCards: Call<ArrayList<CarouselCards>>

    @POST("players")
    fun setPlayerId(@Body params: Player): Call<ResponseBody>

    @GET("jobs")
    fun getJobs(
        @Query("filter[deadline][\$gt]") deadline: String,
        @Query("filter[postedOn][\$lte]") postedOn: String,
        @Query("filter[location][\$ilike][\$any][]") filterLoc: List<String>? = null,
        @Query("filter[type][\$in][]") filterType: List<String>? = null,
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

    @GET("users/me")
    fun getMe(): Call<User>


    /**
     * Admin Side API"s
     */

    @GET("doubts")
    suspend fun getLiveDoubts(
        @Query("exclude") query: String = "content.*",
        @Query("filter[status]") filter: String = "PENDING",
        @Query("include") include: String = "content",
        @Query("page[limit]") page: String = "10",
        @Query("page[offset]") offset: Int = 0,
        @Query("sort") sort: String = "-createdAt"
    ): Response<JSONAPIDocument<List<Doubts>>>

    @GET("doubts")
    suspend fun getMyDoubts(
        @Query("exclude") query: String = "content.*",
        @Query("filter[acknowledgedById]") acknowledgedId: String,
        @Query("filter[status]") filter: String = "ACKNOWLEDGED",
        @Query("include") include: String = "content",
        @Query("page[limit]") page: String = "10",
        @Query("page[offset]") offset: String = "0",
        @Query("sort") sort: String = "-acknowledgedAt"
    ): Response<JSONAPIDocument<List<Doubts>>>

    @GET("doubt_leaderboards")
    suspend fun getLeaderBoard(
        @Query("filter[visible_all]") filter: String = "true",
        @Query("include") include: String = "user",
        @Query("sort") sort: String = "-rating_all",
        @Query("page[limit]") page: String = "10",
        @Query("page[offset]") offset: Int = 0
    ): Response<JSONAPIDocument<List<DoubtLeaderBoard>>>

    @PATCH("doubts/{id}")
    suspend fun acknowledgeDoubt(@Path("id") doubtId: String, @Body params: Doubts): Response<List<Doubts>>

}
