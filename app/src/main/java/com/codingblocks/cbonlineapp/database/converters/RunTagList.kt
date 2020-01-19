package com.codingblocks.cbonlineapp.database.converters

import androidx.room.TypeConverter
import com.codingblocks.onlineapi.models.Tags
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

class RunTagList {
    @TypeConverter
    fun fromTagList(tagList: ArrayList<Tags>): String =
        jacksonObjectMapper().writeValueAsString(tagList)

    @TypeConverter
    fun toTagList(tagList: String): ArrayList<Tags> {
        val mapType = object : TypeReference<ArrayList<Tags>>() {}
        return jacksonObjectMapper().readValue(tagList, mapType)
    }
}
