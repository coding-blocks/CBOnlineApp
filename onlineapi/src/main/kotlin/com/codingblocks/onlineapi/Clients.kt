package com.codingblocks.onlineapi

import com.codingblocks.onlineapi.api.OnlineJsonApi
import com.codingblocks.onlineapi.api.OnlineRestApi
import com.codingblocks.onlineapi.models.Announcement
import com.codingblocks.onlineapi.models.ApplicationId
import com.codingblocks.onlineapi.models.Applications
import com.codingblocks.onlineapi.models.CarouselCards
import com.codingblocks.onlineapi.models.Choice
import com.codingblocks.onlineapi.models.Comment
import com.codingblocks.onlineapi.models.Company
import com.codingblocks.onlineapi.models.ContentCodeChallenge
import com.codingblocks.onlineapi.models.ContentCsv
import com.codingblocks.onlineapi.models.ContentDocumentType
import com.codingblocks.onlineapi.models.ContentLectureType
import com.codingblocks.onlineapi.models.ContentProgress
import com.codingblocks.onlineapi.models.ContentQna
import com.codingblocks.onlineapi.models.ContentVideoType
import com.codingblocks.onlineapi.models.ContentsId
import com.codingblocks.onlineapi.models.Course
import com.codingblocks.onlineapi.models.CourseId
import com.codingblocks.onlineapi.models.DoubtLeaderBoard
import com.codingblocks.onlineapi.models.Doubts
import com.codingblocks.onlineapi.models.Instructor
import com.codingblocks.onlineapi.models.JobId
import com.codingblocks.onlineapi.models.Jobs
import com.codingblocks.onlineapi.models.LectureContent
import com.codingblocks.onlineapi.models.Note
import com.codingblocks.onlineapi.models.Notes
import com.codingblocks.onlineapi.models.Progress
import com.codingblocks.onlineapi.models.Project
import com.codingblocks.onlineapi.models.Question
import com.codingblocks.onlineapi.models.QuizAttempt
import com.codingblocks.onlineapi.models.Quizqnas
import com.codingblocks.onlineapi.models.Quizzes
import com.codingblocks.onlineapi.models.Rating
import com.codingblocks.onlineapi.models.RunAttemptId
import com.codingblocks.onlineapi.models.RunAttempts
import com.codingblocks.onlineapi.models.RunAttemptsId
import com.codingblocks.onlineapi.models.Runs
import com.codingblocks.onlineapi.models.Sections
import com.codingblocks.onlineapi.models.Tags
import com.codingblocks.onlineapi.models.User
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.jasminb.jsonapi.ResourceConverter
import com.github.jasminb.jsonapi.retrofit.JSONAPIConverterFactory
import com.google.gson.GsonBuilder
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.jackson.JacksonConverterFactory
import java.util.concurrent.TimeUnit

object Clients {
    private val om = jacksonObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .setPropertyNamingStrategy(PropertyNamingStrategy.KEBAB_CASE)

    var authJwt = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6MiwiZmlyc3RuYW1lIjoiQWJoaXNoZWsiLCJsYXN0bmFtZSI6Ikd1cHRhIiwidXNlcm5hbWUiOiJhYmhpc2hlazk3IiwiZW1haWwiOiJ6YXBwZXIuYXdha2VAZ21haWwuY29tIiwidmVyaWZpZWRlbWFpbCI6InphcHBlci5hd2FrZUBnbWFpbC5jb20iLCJ2ZXJpZmllZG1vYmlsZSI6bnVsbCwibW9iaWxlIjoiKzkxLTk2NTQ2MjQyNDciLCJvbmVhdXRoX2lkIjoiMTAiLCJsYXN0X3JlYWRfbm90aWZpY2F0aW9uIjoiMCIsInBob3RvIjoiaHR0cHM6Ly9ncmFwaC5mYWNlYm9vay5jb20vMTUwOTk0MjE3OTAzODAyOS9waWN0dXJlP3R5cGU9bGFyZ2UiLCJjb2xsZWdlIjoiMC0tLU9USEVSIC8gTk9UIExJU1RFRCAvIE5PIENPTExFR0UgLS0tMCIsImdyYWR1YXRpb255ZWFyIjoiMjAyNSIsIm9yZ2FuaXphdGlvbiI6bnVsbCwicm9sZUlkIjoxLCJjcmVhdGVkQXQiOiIyMDE3LTA0LTE1VDA2OjQwOjU0LjQwOVoiLCJ1cGRhdGVkQXQiOiIyMDE5LTEyLTI1VDEwOjIzOjU5LjI4M1oiLCJjbGllbnRJZCI6ImQzM2ZmZGU1LTBjOTQtNGJmNi1hOGU2LWI5ZDFiZGVmMjc0OCIsImNsaWVudCI6IndlYiIsImlzVG9rZW5Gb3JBZG1pbiI6ZmFsc2UsImlhdCI6MTU3NzM1MzU2NCwiZXhwIjoxNTc3MzUzODY0fQ.bk30uSgJf2ZggZ96hFS96L6rsnw5z8Ajq1niJdF925GY5vZoZFAdNhOYg7hxGROc5M9a4L54iKmGvDjbCt7Lj8GsNdb-cX6KBFpP2f8Q0DRdXOD860JaA84cMZ8WFgQSwxkFI7_7r3uq7tJFJFMR9IDpnsftAPSmtGgm7e9YsSNPnQigHG8K2ylJR2Ffd1oSkPlK52ojyheAArmfu1XjzVEHYDkd6kkXScLWBIfmfe6x_3-2m6vuXaXuNvp150qCfTs5Dw1TLOx1NjuOwGLUMwW1NJjfOHAq6KfK0D6SoeiVEwfx91_rpF00hfYKugaOjd9sXrXCet7Y1Lsp4neIkw"
    var refreshToken = ""


    private val onlineApiResourceConverter = ResourceConverter(
        om, Instructor::class.java, Course::class.java, Sections::class.java, Runs::class.java, RunAttempts::class.java, ContentVideoType::class.java,
        LectureContent::class.java, ContentDocumentType::class.java, ContentProgress::class.java, ContentLectureType::class.java, ContentCodeChallenge::class.java,
        ContentQna::class.java, Announcement::class.java, Progress::class.java, Quizzes::class.java,
        Question::class.java, Choice::class.java, QuizAttempt::class.java,
        Quizqnas::class.java, Doubts::class.java, ContentCsv::class.java, Comment::class.java,
        Note::class.java, Notes::class.java, Rating::class.java, Tags::class.java, CarouselCards::class.java,
        RunAttemptId::class.java, RunAttemptsId::class.java, ContentsId::class.java,
        Jobs::class.java, Company::class.java, CourseId::class.java, JobId::class.java,
        Applications::class.java, ApplicationId::class.java, DoubtLeaderBoard::class.java, User::class.java,
        Project::class.java
    )

    //type resolver
    init {
        onlineApiResourceConverter.enableDeserializationOption(com.github.jasminb.jsonapi.DeserializationFeature.ALLOW_UNKNOWN_INCLUSIONS)
        onlineApiResourceConverter.enableDeserializationOption(com.github.jasminb.jsonapi.DeserializationFeature.ALLOW_UNKNOWN_TYPE_IN_RELATIONSHIP)
    }

    private const val connectTimeout = 5 // 15s
    private const val readTimeout = 5 // 15s
    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    private val ClientInterceptor = OkHttpClient.Builder()
        .connectTimeout(connectTimeout.toLong(), TimeUnit.SECONDS)
        .readTimeout(readTimeout.toLong(), TimeUnit.SECONDS)
        .connectionPool(ConnectionPool(0, 1, TimeUnit.NANOSECONDS))
        .addInterceptor(logging)
        .addInterceptor { chain ->
            chain.proceed(
                chain.request().newBuilder().addHeader(
                    "Authorization",
                    "JWT $authJwt"
                ).build()
            )
        }
        .build()


    private val onlineV2JsonRetrofit = Retrofit.Builder()
        .client(ClientInterceptor)
        .baseUrl("http://192.168.0.133:3000/api/v2/")
        .addConverterFactory(JSONAPIConverterFactory(onlineApiResourceConverter))
        .addConverterFactory(JacksonConverterFactory.create(om))
        .build()
    val onlineV2JsonApi: OnlineJsonApi
        get() = onlineV2JsonRetrofit
            .create(OnlineJsonApi::class.java)

    var gson = GsonBuilder()
        .setLenient()
        .create()

    private val retrofit = Retrofit.Builder()
        .client(ClientInterceptor)
        .baseUrl("https://api-online.cb.lk/api/")
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
    val api: OnlineRestApi = retrofit.create(OnlineRestApi::class.java)
}
