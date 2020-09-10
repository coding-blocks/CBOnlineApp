package com.codingblocks.onlineapi

import com.github.jasminb.jsonapi.ResourceConverter
import com.github.jasminb.jsonapi.retrofit.JSONAPIConverterFactory
import com.google.gson.GsonBuilder
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type


class JacksonOrJsonConverterFactory(private val onlineApiResourceConverter: ResourceConverter) :
    Converter.Factory() {
    override fun responseBodyConverter(
        type: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, *>? {
        for (annotation in annotations) {
            if (annotation.annotationClass == Jackson::class.java) {
                return JSONAPIConverterFactory(onlineApiResourceConverter)
                    .responseBodyConverter(type, annotations, retrofit)
            }
            if (annotation.annotationClass == Json::class.java) {
                return GsonConverterFactory.create(
                    GsonBuilder().setLenient().excludeFieldsWithoutExposeAnnotation().create()
                ).responseBodyConverter(type, annotations, retrofit)
            }
        }
        return JSONAPIConverterFactory(onlineApiResourceConverter)
            .responseBodyConverter(type, annotations, retrofit)
    }
}