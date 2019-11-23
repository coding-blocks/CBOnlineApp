package com.codingblocks.onlineapi

import com.codingblocks.onlineapi.api.OnlineJsonApi
import com.codingblocks.onlineapi.api.OnlineRestApi
import com.codingblocks.onlineapi.models.*
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.jasminb.jsonapi.ResourceConverter
import com.github.jasminb.jsonapi.retrofit.JSONAPIConverterFactory
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.ConnectionPool
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.jackson.JacksonConverterFactory
import java.util.concurrent.TimeUnit

object Clients {
    private val om = jacksonObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .setPropertyNamingStrategy(PropertyNamingStrategy.KEBAB_CASE)

    var authJwt = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6MjM4NTk0LCJmaXJzdG5hbWUiOiJ0ZXN0IiwibGFzdG5hbWUiOiJkdW1teSIsInVzZXJuYW1lIjoidGVzdGR1bW15IiwiZW1haWwiOiJzYXJ0aGFrajI0OThAZ21haWwuY29tIiwidmVyaWZpZWRlbWFpbCI6InNhcnRoYWtqMjQ5OEBnbWFpbC5jb20iLCJ2ZXJpZmllZG1vYmlsZSI6bnVsbCwibW9iaWxlIjoiKzkxLTEyMzQ1Njc4OTAiLCJvbmVhdXRoX2lkIjoiNDY0MTAiLCJsYXN0X3JlYWRfbm90aWZpY2F0aW9uIjoiMCIsInBob3RvIjpudWxsLCJjb2xsZWdlIjoiQUJFUyBFbmdpbmVlcmluZyBDb2xsZWdlLCBOZXcgRGVsaGkiLCJncmFkdWF0aW9ueWVhciI6IjIwMjMiLCJvcmdhbml6YXRpb24iOm51bGwsInJvbGVJZCI6MywiY3JlYXRlZEF0IjoiMjAxOS0wOC0wNFQxODowMTozOC45NTNaIiwidXBkYXRlZEF0IjoiMjAxOS0xMS0yM1QxMzo1NjoxMy4wMzlaIiwicm9sZSI6eyJpZCI6MywibmFtZSI6Ik1vZGVyYXRvciIsImNyZWF0ZWRBdCI6IjIwMTgtMDktMDRUMTM6Mzg6MzEuODg1WiIsInVwZGF0ZWRBdCI6IjIwMTgtMDktMDRUMTM6Mzg6MzEuODg1WiJ9LCJjbGllbnRJZCI6ImRjODlhZDkzLWNiYTYtNGNmOS1hOGQzLTkxNDU0ODZkYjY5MCIsImNsaWVudCI6IndlYi1hZG1pbiIsImlhdCI6MTU3NDUxNzM3MywiZXhwIjoxNTc0NTE3NjczfQ.X9cux9xVeFof4VU6CRrxBzj0skmROd8zFf0qN7mskTuGh1jTMW_5U_SMQ2lmO34rLVDszxxqPUyXECKsdMSoSZL4EqkOPUrSwp6qbBCmMYtijX0UjVBhbrd9grWvyYcmxBcbDo8H3j9h64UV5Xwl1xkaSj5VlGwQA9Bhi6yxZkETBoIcntEwkffa1oYvj-Dhmwf9mj1IwyA1Ajxs5HeENO0Anr-NiVPgY6KE-gEs5A7GI_nRGk7cK7-HNBRRG4RurelV9WynBdtwqi-dKhiwEGd6COj65U6WURi2383uTJCkDQmOoiDi9h5iIK-mgdzIMelCn7Z-1tbvkEJLMrvaNg"
    var refreshToken = ""


    private val onlineApiResourceConverter = ResourceConverter(
        om, Instructor::class.java, Course::class.java, Sections::class.java, MyCourseRuns::class.java,
        MyCourse::class.java, MyRunAttempts::class.java, MyRunAttempt::class.java, ContentVideoType::class.java,
        LectureContent::class.java, ContentDocumentType::class.java, ContentProgress::class.java,
        CourseSection::class.java, ContentLectureType::class.java, ContentCodeChallenge::class.java,
        ContentQna::class.java, Announcement::class.java, Progress::class.java, Quizzes::class.java,
        Question::class.java, Choice::class.java, QuizAttempt::class.java,
        Quizqnas::class.java, Doubts::class.java, ContentCsv::class.java, Comment::class.java,
        Note::class.java, Notes::class.java, Rating::class.java, Tags::class.java, CarouselCards::class.java,
        RunAttemptId::class.java, RunAttemptsId::class.java, ContentId::class.java, ContentsId::class.java,
        Jobs::class.java, Company::class.java, CourseId::class.java, JobId::class.java,
        Applications::class.java, ApplicationId::class.java
    )

    //type resolver
    init {
        onlineApiResourceConverter.enableDeserializationOption(com.github.jasminb.jsonapi.DeserializationFeature.ALLOW_UNKNOWN_INCLUSIONS)
        onlineApiResourceConverter.enableDeserializationOption(com.github.jasminb.jsonapi.DeserializationFeature.ALLOW_UNKNOWN_TYPE_IN_RELATIONSHIP)
    }

    private const val connectTimeout = 15 // 15s
    private const val readTimeout = 15 // 15s

    private val ClientInterceptor = OkHttpClient.Builder()
        .connectTimeout(connectTimeout.toLong(), TimeUnit.SECONDS)
        .readTimeout(readTimeout.toLong(), TimeUnit.SECONDS)
        .connectionPool(ConnectionPool(0, 1, TimeUnit.NANOSECONDS))
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
        .baseUrl("https://api-online.cb.lk/api/v2/")
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
