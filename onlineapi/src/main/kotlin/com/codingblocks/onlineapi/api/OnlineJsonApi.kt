package com.codingblocks.onlineapi.api

import com.codingblocks.onlineapi.models.Applications
import com.codingblocks.onlineapi.models.Bookmark
import com.codingblocks.onlineapi.models.CareerTracks
import com.codingblocks.onlineapi.models.CarouselCards
import com.codingblocks.onlineapi.models.Comment
import com.codingblocks.onlineapi.models.Company
import com.codingblocks.onlineapi.models.ContentProgress
import com.codingblocks.onlineapi.models.Course
import com.codingblocks.onlineapi.models.DoubtLeaderBoard
import com.codingblocks.onlineapi.models.Doubts
import com.codingblocks.onlineapi.models.Instructor
import com.codingblocks.onlineapi.models.Jobs
import com.codingblocks.onlineapi.models.LectureContent
import com.codingblocks.onlineapi.models.Note
import com.codingblocks.onlineapi.models.Player
import com.codingblocks.onlineapi.models.Professions
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
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
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

    @GET("runs/lastAccessedRun")
    suspend fun getLastAccessed(
        @Query("include") include: String = "course,run_attempts"
    ): Response<Runs>

    @PATCH("doubts/{doubtId}")
    suspend fun getDoubt(
        @Path("doubtId") id: String
    ): Response<Doubts>

    @GET("doubts/{doubtId}/relationships/comments")
    suspend fun getCommentsById(
        @Path("doubtId") id: String
    ): Response<List<Comment>>

    @POST("bookmarks")
    suspend fun addBookmark(
        @Body params: Bookmark
    ): Response<Bookmark>

    @DELETE("bookmarks/{id}")
    suspend fun deleteBookmark(
        @Path("id") id: String
    ): Response<Bookmark>


    @GET("runs")
    suspend fun getMyCourses(
        @Query("enrolled") enrolled: String = "true",
        @Query("page[offset]") offset: String = "0",
        @Query("include") include: String = "course,run_attempts"
    ): Response<JSONAPIDocument<List<Runs>>>


    @GET("instructors/{id}")
    suspend fun getInstructor(@Path("id") id: String): Response<Instructor>

    @GET("instructors/")
    suspend fun getAllInstructors(): Response<List<Instructor>>

    @GET("courses")
    suspend fun getRecommendedCourses(
        @Query("exclude") query: String = "ratings,instructors.*,jobs,runs.*",
        @Query("filter[recommended]") recommended: String = "true",
        @Query("filter[unlisted]") unlisted: String = "false",
        @Query("page[limit]") page: Int = 12,
        @Query("page[offset]") offset: Int = 0,
        @Query("include") include: String = "instructors,runs",
        @Query("sort") sort: String = "difficulty"
    ): Response<List<Course>>

    @GET("courses")
    suspend fun getAllCourses(
        @Query("exclude") query: String = "ratings,instructors.*,jobs,runs.*",
        @Query("filter[unlisted]") unlisted: String = "false",
        @Query("page[offset]") offset: String,
        @Query("include") include: String = "instructors,runs",
        @Query("sort") sort: String = "difficulty"
    ): Response<JSONAPIDocument<List<Course>>>

    @GET("courses")
    suspend fun findCourses(
        @Query("exclude") exclude: String = "ratings,instructors.*",
        @Query("filter[title][\$iLike]") query: String,
        @Query("filter[unlisted]") unlisted: String = "false",
        @Query("page[limit]") page: Int = 100,
        @Query("include") include: String = "instructors,runs"): Response<List<Course>>

    @GET("run_attempts/{runid}")
    suspend fun enrolledCourseById(
        @Path("runid") id: String
    ): Response<RunAttempts>

    @GET("sections/{sectionId}/relationships/contents")
    suspend fun getSectionContents(
        @Path("sectionId") sectionId: String
    ): Response<ArrayList<LectureContent>>

    @GET("run_attempts/{runAttemptId}/relationships/notes")
    suspend fun getNotesByAttemptId(
        @Path("runAttemptId") id: String
    ): Response<List<Note>>

    @GET("bookmarks")
    suspend fun getBookmarksByAttemptId(
        @Query("exclude") query: String = "section.*,content.*",
        @Query("filter[runAttemptId]") id: String,
        @Query("page[limit]") page: Int = 10,
        @Query("page[offset]") offset: Int = 0,
        @Query("include") include: String = "section,content",
        @Query("sort") sort: String = "-createdAt"
    ): Response<List<Bookmark>>

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

    @POST("doubts")
    suspend fun createDoubt(@Body params: Doubts): Response<Doubts>

    @POST("comments")
    suspend fun createComment(@Body params: Comment): Response<Comment>

    @GET("carousel_cards?sort=order")
    suspend fun getCarouselCards(): Response<List<CarouselCards>>

    @POST("players")
    suspend fun setPlayerId(@Body params: Player): Response<ResponseBody>

    @GET("jobs")
    suspend fun getJobs(
        @Query("filter[deadline][\$gt]") deadline: String,
        @Query("filter[postedOn][\$lte]") postedOn: String,
        @Query("filter[location][\$ilike][\$any][]") filterLoc: List<String>? = null,
        @Query("filter[type][\$in][]") filterType: List<String>? = null,
        @Query("page[offset]") pageOffSet: String = "0",
        @Query("page[limit]") pageLimit: String = "12",
        @Query("sort") sort: String = "-postedOn"
    ): Response<JSONAPIDocument<List<Jobs>>>

    @GET("companies/{id}")
    suspend fun getCompany(
        @Path("id") id: String
    ): Response<Company>

    @GET("jobs/{id}")
    fun getJobById(
        @Path("id") id: String
    ): Call<Jobs>

    @POST("applications")
    fun applyJob(@Body params: Applications): Call<ResponseBody>

    @GET("users/me")
    suspend fun getMe(): Response<User>

    @GET("career_tracks")
    suspend fun getTracks(
        @Query("include") include: String = "professions"
    ): Response<List<CareerTracks>>

    @GET("{id}")
    suspend fun getTrackCourses(
        @Path("id") path: String
    ): Response<List<Course>>

    @GET("career_tracks/{id}")
    suspend fun getTrack(
        @Path("id") id: String
    ): Response<CareerTracks>

    @GET("professions")
    suspend fun getProfessions(
    ): Response<List<Professions>>

    @POST("career_tracks/recommend")
    @FormUrlEncoded
    suspend fun getRecommendedTrack(
        @FieldMap params: HashMap<String, String>
    ): Response<CareerTracks>

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
