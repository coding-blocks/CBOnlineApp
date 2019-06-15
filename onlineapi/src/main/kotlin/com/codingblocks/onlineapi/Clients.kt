package com.codingblocks.onlineapi

import com.codingblocks.onlineapi.api.OnlineJsonApi
import com.codingblocks.onlineapi.api.OnlineRestApi
import com.codingblocks.onlineapi.models.Announcement
import com.codingblocks.onlineapi.models.CarouselCards
import com.codingblocks.onlineapi.models.Choice
import com.codingblocks.onlineapi.models.Comment
import com.codingblocks.onlineapi.models.ContentCodeChallenge
import com.codingblocks.onlineapi.models.ContentCsv
import com.codingblocks.onlineapi.models.ContentDocumentType
import com.codingblocks.onlineapi.models.ContentId
import com.codingblocks.onlineapi.models.ContentLectureType
import com.codingblocks.onlineapi.models.ContentProgress
import com.codingblocks.onlineapi.models.ContentQna
import com.codingblocks.onlineapi.models.ContentVideoType
import com.codingblocks.onlineapi.models.ContentsId
import com.codingblocks.onlineapi.models.Course
import com.codingblocks.onlineapi.models.CourseSection
import com.codingblocks.onlineapi.models.CricketChoice
import com.codingblocks.onlineapi.models.CricketChoicePost
import com.codingblocks.onlineapi.models.CricketQuestion
import com.codingblocks.onlineapi.models.CricketQuestionPost
import com.codingblocks.onlineapi.models.DoubtsJsonApi
import com.codingblocks.onlineapi.models.Instructor
import com.codingblocks.onlineapi.models.LectureContent
import com.codingblocks.onlineapi.models.Match
import com.codingblocks.onlineapi.models.MatchPost
import com.codingblocks.onlineapi.models.MyCourse
import com.codingblocks.onlineapi.models.MyCourseRuns
import com.codingblocks.onlineapi.models.MyRunAttempt
import com.codingblocks.onlineapi.models.MyRunAttempts
import com.codingblocks.onlineapi.models.Note
import com.codingblocks.onlineapi.models.Notes
import com.codingblocks.onlineapi.models.Progress
import com.codingblocks.onlineapi.models.Question
import com.codingblocks.onlineapi.models.QuizAttempt
import com.codingblocks.onlineapi.models.Quizqnas
import com.codingblocks.onlineapi.models.Quizzes
import com.codingblocks.onlineapi.models.Rating
import com.codingblocks.onlineapi.models.RunAttemptId
import com.codingblocks.onlineapi.models.RunAttemptsId
import com.codingblocks.onlineapi.models.Sections
import com.codingblocks.onlineapi.models.Tags
import com.codingblocks.onlineapi.models.UserPrediction
import com.codingblocks.onlineapi.models.UserPredictionPost
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.jasminb.jsonapi.RelationshipResolver
import com.github.jasminb.jsonapi.ResourceConverter
import com.github.jasminb.jsonapi.retrofit.JSONAPIConverterFactory
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.jackson.JacksonConverterFactory
import java.io.IOException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit

object Clients {
    private val om = jacksonObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .setPropertyNamingStrategy(PropertyNamingStrategy.KEBAB_CASE)

    var authJwt = ""

    private val onlineApiResourceConverter = ResourceConverter(
        om, Instructor::class.java, Course::class.java, Sections::class.java, MyCourseRuns::class.java,
        MyCourse::class.java, MyRunAttempts::class.java, MyRunAttempt::class.java, ContentVideoType::class.java,
        LectureContent::class.java, ContentDocumentType::class.java, ContentProgress::class.java,
        CourseSection::class.java, ContentLectureType::class.java, ContentCodeChallenge::class.java,
        ContentQna::class.java, Announcement::class.java, Progress::class.java, Quizzes::class.java,
        Question::class.java, Choice::class.java, QuizAttempt::class.java,
        Quizqnas::class.java, DoubtsJsonApi::class.java, ContentCsv::class.java, Comment::class.java,
        Note::class.java, Notes::class.java, Rating::class.java, Tags::class.java, CarouselCards::class.java,
        RunAttemptId::class.java, RunAttemptsId::class.java, ContentId::class.java, ContentsId::class.java,
        UserPrediction::class.java,CricketChoice::class.java,CricketQuestion::class.java,Match::class.java,
        UserPredictionPost::class.java,
        CricketChoicePost::class.java,
        CricketQuestionPost::class.java,
        MatchPost::class.java
    )

    private val relationshipResolver = RelationshipResolver {
        var url = it
        if (!it.contains("https")) {
            url = "https://api-online.cb.lk$url"
        }

        OkHttpClient()
            .newCall(Request.Builder().addHeader("Authorization", "JWT $authJwt").url(url).build())
            .execute()
            .body()
            ?.bytes()
    }

    //type resolver
    init {
        onlineApiResourceConverter.setGlobalResolver(relationshipResolver)
        onlineApiResourceConverter.enableDeserializationOption(com.github.jasminb.jsonapi.DeserializationFeature.ALLOW_UNKNOWN_INCLUSIONS)
        onlineApiResourceConverter.enableDeserializationOption(com.github.jasminb.jsonapi.DeserializationFeature.ALLOW_UNKNOWN_TYPE_IN_RELATIONSHIP)
    }

    private const val connectTimeout = 15 // 15s
    private const val readTimeout = 15 // 15s

    private val ClientInterceptor = OkHttpClient.Builder()
        .connectTimeout(connectTimeout.toLong(), TimeUnit.SECONDS)
        .readTimeout(readTimeout.toLong(), TimeUnit.SECONDS)
        .addInterceptor { chain ->
            chain.proceed(
                chain.request().newBuilder().addHeader(
                    "Authorization",
                    "JWT $authJwt"
                ).build()
            )
        }
        .build()

    @Throws(IOException::class)
    private fun onOnIntercept(chain: Interceptor.Chain): Response {
        try {
            return chain.proceed(chain.request())
        } catch (exception: SocketTimeoutException) {
            exception.printStackTrace()
        }


        return chain.proceed(chain.request())
    }


    private val onlineV2JsonRetrofit = Retrofit.Builder()
        .client(ClientInterceptor)
        .baseUrl("https://api-online.cb.lk/api/v2/")
        .addConverterFactory(JSONAPIConverterFactory(onlineApiResourceConverter))
        .addConverterFactory(JacksonConverterFactory.create(om))
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .build()
    val onlineV2JsonApi: OnlineJsonApi
        get() = onlineV2JsonRetrofit
            .create(OnlineJsonApi::class.java)


    private val retrofit = Retrofit.Builder()
        .client(ClientInterceptor)
        .baseUrl("https://api-online.cb.lk/api/")
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .build()
    val api: OnlineRestApi = retrofit.create(OnlineRestApi::class.java)
}
