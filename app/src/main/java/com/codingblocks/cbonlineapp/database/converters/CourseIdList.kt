package com.codingblocks.cbonlineapp.database.converters

import androidx.room.TypeConverter
import com.codingblocks.onlineapi.models.Course
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

class CourseIdList {
    @TypeConverter
    fun fromListCourseId(companyList: ArrayList<Course>): String =
        jacksonObjectMapper().writeValueAsString(companyList)

    @TypeConverter
    fun toListCourseId(courseIdList: String): ArrayList<Course> {
        val mapType = object : TypeReference<ArrayList<Course>>() {}
        return jacksonObjectMapper().readValue(courseIdList, mapType)
    }
}
