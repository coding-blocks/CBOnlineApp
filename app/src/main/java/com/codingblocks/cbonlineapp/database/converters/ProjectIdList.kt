package com.codingblocks.cbonlineapp.database.converters

import androidx.room.TypeConverter
import com.codingblocks.onlineapi.models.Project
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

class ProjectIdList {
    @TypeConverter
    fun fromProjectIdList(projectIdList: ArrayList<Project>): String =
        jacksonObjectMapper().writeValueAsString(projectIdList)

    @TypeConverter
    fun toProjectIdList(projectIdList: String): ArrayList<Project> {
        val mapType = object : TypeReference<ArrayList<Project>>() {}
        return jacksonObjectMapper().readValue(projectIdList, mapType)
    }
}
