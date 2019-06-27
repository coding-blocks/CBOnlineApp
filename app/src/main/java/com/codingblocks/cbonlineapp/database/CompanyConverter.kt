package com.codingblocks.cbonlineapp.database

import androidx.room.TypeConverter
import com.codingblocks.onlineapi.models.Company
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

class ListCompanyConverter {

    @TypeConverter
    fun fromListCopmany(companyList: ArrayList<Company>): String {
        val objectMapper = jacksonObjectMapper()
        return objectMapper.writeValueAsString(companyList)
    }

    @TypeConverter
    fun toListCompany(companyList: String): ArrayList<Company> {
        val objectMapper = jacksonObjectMapper()
        val mapType = object : TypeReference<ArrayList<Company>>() {}
        return objectMapper.readValue(companyList, mapType)
    }
}
