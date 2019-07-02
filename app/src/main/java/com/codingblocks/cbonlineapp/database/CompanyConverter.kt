package com.codingblocks.cbonlineapp.database

import androidx.room.TypeConverter
import com.codingblocks.onlineapi.models.Form
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper

class ListCompanyConverter {

    @TypeConverter
    fun fromListForm(companyList: ArrayList<Form>): String {
        val objectMapper = ObjectMapper()
        return objectMapper.writeValueAsString(companyList)
    }

    @TypeConverter
    fun toListForm(companyList: String): ArrayList<Form> {
        val objectMapper = ObjectMapper()
        val mapType = object : TypeReference<ArrayList<Form>>() {}
        return objectMapper.readValue(companyList, mapType)
    }
}
