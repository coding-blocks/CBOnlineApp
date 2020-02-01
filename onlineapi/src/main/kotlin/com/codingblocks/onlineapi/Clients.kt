package com.codingblocks.onlineapi

import com.codingblocks.onlineapi.api.OnlineJsonApi
import com.codingblocks.onlineapi.api.OnlineRestApi
import com.codingblocks.onlineapi.models.Announcement
import com.codingblocks.onlineapi.models.ApplicationId
import com.codingblocks.onlineapi.models.Applications
import com.codingblocks.onlineapi.models.Bookmark
import com.codingblocks.onlineapi.models.CareerTracks
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
import com.codingblocks.onlineapi.models.Professions
import com.codingblocks.onlineapi.models.Project
import com.codingblocks.onlineapi.models.Question
import com.codingblocks.onlineapi.models.QuizAttempt
import com.codingblocks.onlineapi.models.Quizzes
import com.codingblocks.onlineapi.models.Rating
import com.codingblocks.onlineapi.models.RunAttempts
import com.codingblocks.onlineapi.models.Runs
import com.codingblocks.onlineapi.models.Sections
import com.codingblocks.onlineapi.models.Tags
import com.codingblocks.onlineapi.models.User
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonSetter
import com.fasterxml.jackson.annotation.Nulls
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.module.kotlin.KotlinModule
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
        .setSerializationInclusion(JsonInclude.Include.NON_NULL)
//         Skip all null assignments (unless overridden)
        .setDefaultSetterInfo(JsonSetter.Value.forValueNulls(Nulls.SKIP))
        .registerModule(KotlinModule()) //let Jackson know about Kotlin
    const val localJwt =
        "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6Mzc5NzUsImZpcnN0bmFtZSI6IlB1bGtpdCIsImxhc3RuYW1lIjoiQWdnYXJ3YWwiLCJ1c2VybmFtZSI6ImFnZ2Fyd2FscHVsa2l0NTk2LWciLCJlbWFpbCI6ImFnZ2Fyd2FscHVsa2l0NTk2QGdtYWlsLmNvbSIsInZlcmlmaWVkZW1haWwiOiJhZ2dhcndhbHB1bGtpdDU5NkBnbWFpbC5jb20iLCJ2ZXJpZmllZG1vYmlsZSI6Iis5MS05NTgyMDU0NjY0IiwibW9iaWxlIjoiKzkxLTk1ODIwNTQ2NjQiLCJvbmVhdXRoX2lkIjoiMTIwMzUiLCJsYXN0X3JlYWRfbm90aWZpY2F0aW9uIjoiMCIsInBob3RvIjoiaHR0cHM6Ly9taW5pby5jb2RpbmdibG9ja3MuY29tL2ltZy9hdmF0YXItMjAuc3ZnIiwiY29sbGVnZSI6IkFtaXR5IFNjaG9vbCBPZiBFbmdpbmVlcmluZyAmIFRlY2hub2xvZ3kgKE5vaWRhKSIsImdyYWR1YXRpb255ZWFyIjoiMjAyNSIsIm9yZ2FuaXphdGlvbiI6bnVsbCwicm9sZUlkIjoxLCJjcmVhdGVkQXQiOiIyMDE4LTA5LTI3VDEzOjEwOjU5LjM5NloiLCJ1cGRhdGVkQXQiOiIyMDIwLTAxLTEzVDEwOjA5OjIyLjYxOVoiLCJjbGllbnRJZCI6IjI0M2FmZjEyLTRiOWMtNGJlMC05M2IwLWY1NjIwZDRhNWFjYSIsImNsaWVudCI6IndlYiIsImlzVG9rZW5Gb3JBZG1pbiI6ZmFsc2UsImlhdCI6MTU3ODk3NTMxMywiZXhwIjoxNTc4OTc1NjEzfQ.rr3zDi5U9n-lYxypNa2IR3pJZYn8Bk2tYPMJFVM28EesFML4yNcxVZDmYL7oTuQELMsQK5ZQjmzi8rBJ0Yvi649tVW393nY4ieAI5NDh7-bp6T8okoPCKK2A2nWJeoruLgo2tM0YR6UmXTmw-U0PBVoyrGYIn5I3Y5Mv3QE8GzJVLLsx7GHDYvvLPNlM6HR2KBygqc3W6d5bCW06E9RWjd5KXACsUNNGga6xv5deZP8iRZ8tnS59lcstkng4B11CSURqPI8RW4aVTn1ggRgtMS3WG88lv6lMz-u3F-62EKEikiUEdGDxnDm4-qa5SzEgXvLvbhL-dbHDr47umQMGMA"
    private const val debugJwt =
        "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6Mzc5NzUsImZpcnN0bmFtZSI6IlB1bGtpdCIsImxhc3RuYW1lIjoiQWdnYXJ3YWwiLCJ1c2VybmFtZSI6ImFnZ2Fyd2FscHVsa2l0NTk2LWciLCJlbWFpbCI6ImFnZ2Fyd2FscHVsa2l0NTk2QGdtYWlsLmNvbSIsInZlcmlmaWVkZW1haWwiOiJhZ2dhcndhbHB1bGtpdDU5NkBnbWFpbC5jb20iLCJ2ZXJpZmllZG1vYmlsZSI6Iis5MS05NTgyMDU0NjY0IiwibW9iaWxlIjoiKzkxLTk1ODIwNTQ2NjQiLCJvbmVhdXRoX2lkIjoiMTIwMzUiLCJsYXN0X3JlYWRfbm90aWZpY2F0aW9uIjoiMCIsInBob3RvIjoiaHR0cHM6Ly9taW5pby5jb2RpbmdibG9ja3MuY29tL2ltZy9hdmF0YXItMjAuc3ZnIiwiY29sbGVnZSI6IkFtaXR5IFNjaG9vbCBPZiBFbmdpbmVlcmluZyAmIFRlY2hub2xvZ3kgKE5vaWRhKSIsImdyYWR1YXRpb255ZWFyIjoiMjAyNSIsIm9yZ2FuaXphdGlvbiI6bnVsbCwicm9sZUlkIjoxLCJjcmVhdGVkQXQiOiIyMDE4LTA5LTI3VDEzOjEwOjU5LjM5NloiLCJ1cGRhdGVkQXQiOiIyMDIwLTAxLTA5VDA5OjAwOjA2LjIzM1oiLCJyb2xlIjp7ImlkIjoxLCJuYW1lIjoiQWRtaW4iLCJjcmVhdGVkQXQiOiIyMDE3LTA5LTA3VDEwOjU4OjE5Ljk5M1oiLCJ1cGRhdGVkQXQiOiIyMDE3LTA5LTA3VDEwOjU4OjE5Ljk5M1oifSwiY2xpZW50SWQiOiJjNGQ1M2U1NS01MTBmLTQ0NzktOTE3ZS1hZDBhMzNhMzNmNTEiLCJjbGllbnQiOiJ3ZWIiLCJpYXQiOjE1Nzg1NjA0MDYsImV4cCI6MTU3ODU2MDcwNn0.mepYTorI-Ui-J4WENRkSOlHuVuN-iIg_oEDbL9T4wl4YITQWp4hfOLdsJGw6caEL0n3VL_4ghrL-mnXc5Q1yPUaHMg2XSGtV9VNupztcuFHGyPfRU6S8r-WM971pLKhl_k_SM6eihsT9FQImzOK20RZA31uGHZL8tVtVpPzGLsTBdmhJTPh_uAluMxvwWuN31Krx3NY2ecVo8fg_2phtWsYeL2vSW67O5h8MDWOjumR9o0CWHM9kpon5XYknn1q79_YNANpNUaw3dpETPYuU8E2851i3m-s1TEw8WXudP7g7RfQd23Rt0E8J2KYhvZKu6SEo0G8yzs_-BXX8yq3S1A"
    var authJwt = ""
    var refreshToken = ""


    private val onlineApiResourceConverter = ResourceConverter(
        om,
        Instructor::class.java,
        Course::class.java,
        Sections::class.java,
        Runs::class.java,
        RunAttempts::class.java,
        ContentVideoType::class.java,
        LectureContent::class.java,
        ContentDocumentType::class.java,
        ContentProgress::class.java,
        ContentLectureType::class.java,
        ContentCodeChallenge::class.java,
        ContentQna::class.java,
        Announcement::class.java,
        Quizzes::class.java,
        Bookmark::class.java,
        Professions::class.java,
        Question::class.java,
        Choice::class.java,
        QuizAttempt::class.java,
        Doubts::class.java,
        ContentCsv::class.java,
        Comment::class.java,
        Note::class.java,
        Rating::class.java,
        Tags::class.java,
        CarouselCards::class.java,
        Jobs::class.java,
        Company::class.java,
        JobId::class.java,
        Applications::class.java,
        ApplicationId::class.java,
        DoubtLeaderBoard::class.java,
        User::class.java,
        CareerTracks::class.java,
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
        level = HttpLoggingInterceptor.Level.NONE
    }

    fun setHttpLogging(enabled: Boolean) {
        logging.level =
            if (enabled)
                HttpLoggingInterceptor.Level.BODY
            else
                HttpLoggingInterceptor.Level.NONE
    }

    private val ClientInterceptor = OkHttpClient.Builder()
        .connectTimeout(connectTimeout.toLong(), TimeUnit.SECONDS)
        .readTimeout(readTimeout.toLong(), TimeUnit.SECONDS)
        .connectionPool(ConnectionPool(0, 1, TimeUnit.NANOSECONDS))
        .addInterceptor(logging)
        .addInterceptor { chain ->
            if (authJwt.isEmpty())
                chain.proceed(chain.request())
            else chain.proceed(chain.request().newBuilder()
                .addHeader("Authorization", "JWT $authJwt").build()
            )
        }
        .build()

    private const val LOCAL = "192.168.0.101:3000"
    private const val DEBUG = "api-online.codingblocks.xyz"
    private const val PROD = "online-api.codingblocks.com"


    private val onlineV2JsonRetrofit = Retrofit.Builder()
        .client(ClientInterceptor)
        .baseUrl("https://$PROD/api/v2/")
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
        .baseUrl("https://$PROD/api/")
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
    val api: OnlineRestApi = retrofit.create(OnlineRestApi::class.java)
}
