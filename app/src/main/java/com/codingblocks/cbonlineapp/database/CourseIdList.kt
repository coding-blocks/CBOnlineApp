package com.codingblocks.cbonlineapp.database

import androidx.room.TypeConverter
import com.codingblocks.onlineapi.models.CourseId
import com.codingblocks.onlineapi.models.Form
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper

class CourseIdList{
    @TypeConverter
    fun fromListCourseId(companyList: ArrayList<CourseId>): String {
        val objectMapper = ObjectMapper()
        return objectMapper.writeValueAsString(companyList)
    }

    @TypeConverter
    fun toListCourseId(courseIdList: String): ArrayList<CourseId> {
        val objectMapper = ObjectMapper()
        val mapType = object : TypeReference<ArrayList<CourseId>>() {}
        return objectMapper.readValue(courseIdList, mapType)
    }
}
