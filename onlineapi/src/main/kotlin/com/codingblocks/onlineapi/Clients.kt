package com.codingblocks.onlineapi

import com.codingblocks.onlineapi.api.OnlinePublicApi
import com.codingblocks.onlineapi.models.Contents
import com.codingblocks.onlineapi.models.Course
import com.codingblocks.onlineapi.models.Instructor
import com.codingblocks.onlineapi.models.Sections
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.jasminb.jsonapi.RelationshipResolver
import com.github.jasminb.jsonapi.ResourceConverter
import com.github.jasminb.jsonapi.SerializationFeature
import com.github.jasminb.jsonapi.retrofit.JSONAPIConverterFactory
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit

object Clients {
    private val om = ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false)

    private val onlineApiResourceConverter = ResourceConverter(
            om,
            Instructor::class.java,
            Course::class.java,
            Sections::class.java,
            Contents::class.java
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
//        onlineApiResourceConverter.enableSerializationOption(SerializationFeature.INCLUDE_RELATIONSHIP_ATTRIBUTES);
    }


    val onlineV2PublicClient: OnlinePublicApi
        get() = Retrofit.Builder()
                .baseUrl("https://api-online.cb.lk/api/v2/")
                .addConverterFactory(JSONAPIConverterFactory(onlineApiResourceConverter))
                .build()
                .create(OnlinePublicApi::class.java)

}