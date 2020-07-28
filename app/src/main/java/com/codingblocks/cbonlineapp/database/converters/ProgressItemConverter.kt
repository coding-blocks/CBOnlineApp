package com.codingblocks.cbonlineapp.database.converters

import androidx.room.TypeConverter
import com.codingblocks.onlineapi.models.ProgressItem
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

/**
 * @author aggarwalpulkit596
 */
class ProgressItemConverter {
    @TypeConverter
    fun fromProgressList(progressList: ArrayList<ProgressItem>): String =
        jacksonObjectMapper().writeValueAsString(progressList)

    @TypeConverter
    fun toProgressList(progressList: String): ArrayList<ProgressItem> {
        val mapType = object : TypeReference<ArrayList<ProgressItem>>() {}
        return jacksonObjectMapper().readValue(progressList, mapType)
    }
}
