package com.codingblocks.onlineapi.models

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.github.jasminb.jsonapi.annotations.Id
import com.github.jasminb.jsonapi.annotations.Relationship
import com.github.jasminb.jsonapi.annotations.Type

open class BaseModel {
    @Id @JvmField
    var id: String? = null
}

@Type("instructors")
class Instructor: BaseModel() {
    @JvmField
    var name: String? = null
    @JvmField
    var description: String? = null
    @JvmField
    var photo: String? = null

    @Relationship("courses", resolve = true)
    @JvmField
    var courses: ArrayList<Course>? = null
}

@Type("courses")
class Course: BaseModel() {
    @JvmField
    var title: String? = null
    @JvmField
    var subtitle: String? = null
    @JvmField
    var logo: String? = null
    @JvmField
    var summary: String? = null

    @JvmField @JsonProperty("category-name")
    var categoryName: String? = null

    @JvmField @JsonProperty("category-id")
    var categoryId: Int? = null

    @Relationship("instructors", resolve = true)
    @JvmField
    var instructors: ArrayList<Instructor>? = null

}
