package com.codingblocks.onlineapi

import com.codingblocks.onlineapi.api.OnlineJsonApi
import com.codingblocks.onlineapi.api.OnlineRestApi
import com.codingblocks.onlineapi.models.*
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonSetter
import com.fasterxml.jackson.annotation.Nulls
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.jasminb.jsonapi.ResourceConverter
import com.github.jasminb.jsonapi.retrofit.JSONAPIConverterFactory
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.jackson.JacksonConverterFactory
import java.util.concurrent.TimeUnit

class Clients internal constructor(
    private val communicator: CBOnlineCommunicator
) {
    companion object {
        private const val LOCAL = "192.168.1.13:3000"
        private const val DEBUG = "api-online.codingblocks.xyz"
        private const val PROD = "online-api.codingblocks.com"
        private const val URL = "online-api.codingblocks.com"
        const val CONNECT_TIMEOUT = 15
        const val READ_TIMEOUT = 15
    }


    private val om = jacksonObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .setPropertyNamingStrategy(PropertyNamingStrategy.KEBAB_CASE)
        .setSerializationInclusion(JsonInclude.Include.NON_NULL)
        .setDefaultSetterInfo(JsonSetter.Value.forValueNulls(Nulls.SKIP))
        .registerModule(KotlinModule()) // let Jackson know about Kotlin

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
        Project::class.java,
        Player::class.java,
        CodeChallenge::class.java,
        SpinPrize::class.java,
        Spins::class.java
    )

    // type resolver
    init {
        onlineApiResourceConverter.enableDeserializationOption(com.github.jasminb.jsonapi.DeserializationFeature.ALLOW_UNKNOWN_INCLUSIONS)
        onlineApiResourceConverter.enableDeserializationOption(com.github.jasminb.jsonapi.DeserializationFeature.ALLOW_UNKNOWN_TYPE_IN_RELATIONSHIP)
    }

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
    fun getHttpLogging(): Boolean = when (logging.level) {
        HttpLoggingInterceptor.Level.BODY -> true
        else -> false
    }

    private val clientInterceptor = OkHttpClient.Builder()
        .connectTimeout(CONNECT_TIMEOUT.toLong(), TimeUnit.SECONDS)
        .readTimeout(READ_TIMEOUT.toLong(), TimeUnit.SECONDS)
        .connectionPool(ConnectionPool(0, 1, TimeUnit.NANOSECONDS))
        .addInterceptor(logging)
        .addInterceptor { chain ->
            if (communicator.authJwt.isEmpty())
                chain.proceed(chain.request())
            else chain.proceed(chain.request().newBuilder()
                .addHeader("Authorization", "JWT ${communicator.authJwt}").build()
            )
        }
        .build()

    private val onlineV2JsonRetrofit = Retrofit.Builder()
        .client(clientInterceptor)
        .baseUrl("http://$PROD/api/v2/")
        .addConverterFactory(JSONAPIConverterFactory(onlineApiResourceConverter))
        .addConverterFactory(JacksonConverterFactory.create(om))
        .build()
    val onlineV2JsonApi: OnlineJsonApi
        get() = onlineV2JsonRetrofit
            .create(OnlineJsonApi::class.java)

    var gson: Gson = GsonBuilder()
        .setLenient()
        .create()

    private val retrofit = Retrofit.Builder()
        .client(clientInterceptor)
        .baseUrl("http://$PROD/api/")
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
    val api: OnlineRestApi = retrofit.create(OnlineRestApi::class.java)
}
