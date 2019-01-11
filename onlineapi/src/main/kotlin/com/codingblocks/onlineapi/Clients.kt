package com.codingblocks.onlineapi

import com.codingblocks.onlineapi.api.OnlineJsonApi
import com.codingblocks.onlineapi.api.OnlineRestApi
import com.codingblocks.onlineapi.api.OnlineVideosApi
import com.codingblocks.onlineapi.models.*
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.jasminb.jsonapi.RelationshipResolver
import com.github.jasminb.jsonapi.ResourceConverter
import com.github.jasminb.jsonapi.SerializationFeature
import com.github.jasminb.jsonapi.retrofit.JSONAPIConverterFactory
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Clients {
    private val om = ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false)

    var authJwt = ""

    private val onlineApiResourceConverter = ResourceConverter(
            om,
            Instructor::class.java,
            Course::class.java,
            Sections::class.java,
            Contents::class.java,
            MyCourseRuns::class.java,
            MyCourse::class.java,
            MyRunAttempts::class.java,
            MyRunAttempt::class.java,
            ContentVideoType::class.java,
            LectureContent::class.java,
            ContentDocumentType::class.java,
            ContentProgress::class.java,
            CourseSection::class.java,
            ContentLectureType::class.java,
            InstructorSingle::class.java,
            ContentCodeChallenge::class.java,
            ContentQna::class.java,
            Announcement::class.java,
            Progress::class.java,
            Quizzes::class.java,
            Question::class.java,
            Choice::class.java,
            QuizAttempt::class.java,
            QuizRunAttempt::class.java,
            Quizqnas::class.java


    )
    private val relationshipResolver = RelationshipResolver {
        OkHttpClient()
                .newCall(Request.Builder().url(it).build())
                .execute()
                .body()
                ?.bytes()
    }

    init {
        onlineApiResourceConverter.setGlobalResolver(relationshipResolver)
        onlineApiResourceConverter.disableSerializationOption(SerializationFeature.INCLUDE_RELATIONSHIP_ATTRIBUTES)
    }

    private val ClientInterceptor = OkHttpClient.Builder()
            .addInterceptor { chain ->
                chain.proceed(chain.request().newBuilder().addHeader("Authorization", "JWT $authJwt").build())
            }
            .build()


    private val onlineV2JsonRetrofit = Retrofit.Builder()
            .client(ClientInterceptor)
            .baseUrl("https://api-online.cb.lk/api/v2/")
            .addConverterFactory(JSONAPIConverterFactory(onlineApiResourceConverter))
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()

    val onlineV2JsonApi: OnlineJsonApi
        get() = onlineV2JsonRetrofit
                .create(OnlineJsonApi::class.java)


    private val retrofit = Retrofit.Builder()
            .client(ClientInterceptor)
            .baseUrl("https://api-online.cb.lk/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    val api: OnlineRestApi = retrofit.create(OnlineRestApi::class.java)

    var interceptor = CustomResponseInterceptor()

    private var client = OkHttpClient.Builder().addInterceptor(interceptor).build()

    //This client will download the video and m3u8 files from the server
    private val videoDownloadClient = Retrofit.Builder()
            .baseUrl("https://d1qf0ozss494xv.cloudfront.net/")
            .client(client)
            .build()

    private val apiVideo: OnlineVideosApi = videoDownloadClient.create(OnlineVideosApi::class.java)

    fun initiateDownload(url: String, fileName: String) = apiVideo.getVideoFiles(url, fileName)
}