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
import com.codingblocks.onlineapi.models.Course
import com.codingblocks.onlineapi.models.DoubtLeaderBoard
import com.codingblocks.onlineapi.models.Doubts
import com.codingblocks.onlineapi.models.Instructor
import com.codingblocks.onlineapi.models.JobId
import com.codingblocks.onlineapi.models.Jobs
import com.codingblocks.onlineapi.models.LectureContent
import com.codingblocks.onlineapi.models.Note
import com.codingblocks.onlineapi.models.Progress
import com.codingblocks.onlineapi.models.Project
import com.codingblocks.onlineapi.models.Question
import com.codingblocks.onlineapi.models.QuizAttempt
import com.codingblocks.onlineapi.models.Quizqnas
import com.codingblocks.onlineapi.models.Quizzes
import com.codingblocks.onlineapi.models.Rating
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

    var authJwt = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6Mzc5NzUsImZpcnN0bmFtZSI6IlB1bGtpdCIsImxhc3RuYW1lIjoiQWdnYXJ3YWwiLCJ1c2VybmFtZSI6ImFnZ2Fyd2FscHVsa2l0NTk2LWciLCJlbWFpbCI6ImFnZ2Fyd2FscHVsa2l0NTk2QGdtYWlsLmNvbSIsInZlcmlmaWVkZW1haWwiOiJhZ2dhcndhbHB1bGtpdDU5NkBnbWFpbC5jb20iLCJ2ZXJpZmllZG1vYmlsZSI6Iis5MS05NTgyMDU0NjY0IiwibW9iaWxlIjoiKzkxLTk1ODIwNTQ2NjQiLCJvbmVhdXRoX2lkIjoiMTIwMzUiLCJsYXN0X3JlYWRfbm90aWZpY2F0aW9uIjoiMCIsInBob3RvIjoiaHR0cHM6Ly9taW5pby5jb2RpbmdibG9ja3MuY29tL2ltZy9hdmF0YXItMjAuc3ZnIiwiY29sbGVnZSI6IkFtaXR5IFNjaG9vbCBPZiBFbmdpbmVlcmluZyAmIFRlY2hub2xvZ3kgKE5vaWRhKSIsImdyYWR1YXRpb255ZWFyIjoiMjAyNSIsIm9yZ2FuaXphdGlvbiI6bnVsbCwicm9sZUlkIjoxLCJjcmVhdGVkQXQiOiIyMDE4LTA5LTI3VDEzOjEwOjU5LjM5NloiLCJ1cGRhdGVkQXQiOiIyMDE5LTEyLTI3VDEyOjQyOjAzLjMwOFoiLCJjbGllbnRJZCI6ImY0Yjg5MTczLThmOTMtNGUwYS04OTEzLTA5OTAzOWIzY2U2NyIsImNsaWVudCI6IndlYiIsImlzVG9rZW5Gb3JBZG1pbiI6ZmFsc2UsImlhdCI6MTU3NzQ1OTU0MywiZXhwIjoxNTc3NDU5ODQzfQ.DNzePL0hiD8LCVv2R92S_8MSpZQbjFNgQgSZl148MCMHbXDXfWgTnm1fp7RohyqwOR0349_55N4wue6c5JM0x-pCuuEhcdlsl-AMm2j8obMMZs_a2D55LUjD_6QqkNBRAUXktyR-WG8oHU7qSsTcus8ns1wHD5Pfk21zCt0Jv3d0fMT0QtNUsFLlyok6BbfbusnXbZfDAleQsTnZZLD4tz2X0SRMq_J_quyyCX3Sqp2OcexA8eswYLfqa6aQBL3W3F-57J7_6EW0QSs75AlJ1Ul0Jm7SfNtzU5Ie-__OWmTBugdvPuE7ZNOJUj07BVYeXVDm9WDF6yC-V9bVro7B5Q"
    var refreshToken = ""


    private val onlineApiResourceConverter = ResourceConverter(
        om, Instructor::class.java, Course::class.java, Sections::class.java, Runs::class.java, RunAttempts::class.java, ContentVideoType::class.java,
        LectureContent::class.java, ContentDocumentType::class.java, ContentProgress::class.java, ContentLectureType::class.java, ContentCodeChallenge::class.java,
        ContentQna::class.java, Announcement::class.java, Progress::class.java, Quizzes::class.java,
        Question::class.java, Choice::class.java, QuizAttempt::class.java,
        Quizqnas::class.java, Doubts::class.java, ContentCsv::class.java, Comment::class.java,
        Note::class.java, Rating::class.java, Tags::class.java, CarouselCards::class.java,
        RunAttemptsId::class.java, Jobs::class.java, Company::class.java, JobId::class.java,
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
        .baseUrl("http://192.168.1.8:3000/api/v2/")
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
