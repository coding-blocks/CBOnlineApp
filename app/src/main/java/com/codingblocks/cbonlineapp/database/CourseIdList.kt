package com.codingblocks.cbonlineapp.database

import androidx.room.TypeConverter
import com.codingblocks.onlineapi.models.CourseId
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

class CourseIdList {
    @TypeConverter
    fun fromListCourseId(companyList: ArrayList<CourseId>): String =
        jacksonObjectMapper().writeValueAsString(companyList)

    @TypeConverter
    fun toListCourseId(courseIdList: String): ArrayList<CourseId> {
        val mapType = object : TypeReference<ArrayList<CourseId>>() {}
        return jacksonObjectMapper().readValue(courseIdList, mapType)
    }
}
