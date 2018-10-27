package com.codingblocks.onlineapi.models

import com.fasterxml.jackson.annotation.JsonProperty
import com.github.jasminb.jsonapi.annotations.Id
import com.github.jasminb.jsonapi.annotations.Relationship
import com.github.jasminb.jsonapi.annotations.Type


open class BaseModel {
    @Id
    @JvmField
    var id: String? = null
}

@Type("instructors")
class Instructor : BaseModel() {
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

@Type("sections")
class Sections : BaseModel() {
    @JvmField
    var name: String? = null
    @JvmField
    var preminum: Boolean? = false
    @JvmField
    var status: String? = null

    @Relationship("contents", resolve = true)
    @JvmField
    var contents: ArrayList<Contents>? = null
}

@Type("contents")
class Contents : BaseModel() {
    @JvmField
    var contentable: String? = null
    @JvmField
    var duration: Long? = null
    @JvmField
    var title: String? = null

    @JsonProperty("section-content")
    @JvmField
    val sectionContent: SectionContent? = null

}

class SectionContent {
    val id: String? = null
    val order: Int? = null
    val createdAt: String? = null
    val updatedAt: String? = null
    val sectionId: String? = null
    val contentId: String? = null
    val updatedById: String? = null
}


@Type("runs")
class Runs : BaseModel() {
    @JvmField

    var name: String? = null
    @JvmField
    var description: String? = null
    @JvmField
    var start: String? = null
    @JvmField
    var end: String? = null
    @JvmField
    var price: String? = null
    @JvmField
    var mrp: String? = null
    @JvmField
    @JsonProperty("enrollment-start")
    val enrollmentStart: String? = null
    @JvmField
    @JsonProperty("enrollment-end")
    val enrollmentEnd: String? = null
    @Relationship("sections", resolve = true)
    @JvmField
    var sections: ArrayList<Sections>? = null

}


@Type("courses")
class Course : BaseModel() {
    @JvmField
    var title: String? = null
    @JvmField
    var subtitle: String? = null
    @JvmField
    var logo: String? = null
    @JvmField
    var summary: String? = null

    @JvmField
    @JsonProperty("category-name")
    var categoryName: String? = null

    @JvmField
    @JsonProperty("category-id")
    var categoryId: Int? = null

    @JvmField
    @JsonProperty("promo-video")
    var promoVideo: String? = null

    @JvmField
    @JsonProperty("review-count")
    var reviewCount: Int? = null

    @JvmField
    var difficulty: String? = null

    @JvmField
    var rating: Float? = null

    @JvmField
    var slug: String? = null

    @JvmField
    @JsonProperty("cover-image")
    var coverImage: String? = null

    @Relationship("instructors", resolve = true)
    @JvmField
    var instructors: ArrayList<Instructor>? = null

    @Relationship("runs", resolve = true)
    @JvmField
    var runs: ArrayList<Runs>? = null

}

@Type("run")
class MyCourseRuns : BaseModel() {
    @JvmField

    var name: String? = null
    @JvmField
    var description: String? = null
    @JvmField
    var start: String? = null
    @JvmField
    var end: String? = null
    @JvmField
    var price: String? = null
    @JvmField
    var mrp: String? = null
    @JvmField
    @JsonProperty("enrollment-start")
    val enrollmentStart: String? = null
    @JvmField
    @JsonProperty("enrollment-end")
    val enrollmentEnd: String? = null
    @Relationship("course", resolve = true)
    @JvmField
    var course: MyCourse? = null

    @Relationship("run-attempts", resolve = true)
    @JvmField
    var run_attempts: ArrayList<MyRunAttempts>? = null

}

@Type("run_attempts")
class MyRunAttempts : BaseModel() {

    @JvmField
    @JsonProperty("certificate-approved")
    var certificate_approved: Boolean? = false
    @JvmField
    var end: String? = null
    @JvmField
    var premium: Boolean? = false
    @JvmField
    var revoked: Boolean? = false
    @Relationship("run", resolve = true)
    @JvmField
    var run: MyCourseRuns? = null

}

@Type("run_attempt")
class MyRunAttempt : BaseModel() {

    @JvmField
    @JsonProperty("certificate-approved")
    var certificate_approved: Boolean? = false
    @JvmField
    var end: String? = null
    @JvmField
    var premium: Boolean? = false
    @JvmField
    var revoked: Boolean? = false
    @Relationship("run", resolve = true)
    @JvmField
    var run: MyCourseRuns? = null

    @JsonProperty("videos")
    @JvmField
    var videos: ArrayList<LectureVideo>? = null

    @JsonProperty("content")
    @JvmField
    var contents: ArrayList<LectureContent>? = null

    @JsonProperty("document")
    @JvmField
    var documents: ArrayList<LectureDocument>? = null

}

@Type("course")
class MyCourse : BaseModel() {
    @JvmField
    var title: String? = null
    @JvmField
    var subtitle: String? = null
    @JvmField
    var logo: String? = null
    @JvmField
    var summary: String? = null

    @JvmField
    @JsonProperty("category-name")
    var categoryName: String? = null

    @JvmField
    @JsonProperty("category-id")
    var categoryId: Int? = null

    @JvmField
    @JsonProperty("promo-video")
    var promoVideo: String? = null

    @JvmField
    @JsonProperty("review-count")
    var reviewCount: Int? = null

    @JvmField
    var difficulty: String? = null

    @JvmField
    var rating: Float? = null

    @JvmField
    var slug: String? = null

    @JvmField
    @JsonProperty("cover-image")
    var coverImage: String? = null

    @Relationship("instructors", resolve = false)
    @JvmField
    var instructors: ArrayList<Instructor>? = null

    @Relationship("runs", resolve = true)
    @JvmField
    var runs: ArrayList<Runs>? = null

}

@Type("video")
class LectureVideo : BaseModel() {
    @JvmField
    var description: String? = null

    @JvmField
    @JsonProperty("content-id")
    var content_id: String? = null

    @JvmField
    var duration: Long? = null

    @JvmField
    var name: String? = null

    @JvmField
    var url: String? = null

    @JvmField
    @JsonProperty("cover-image")
    var coverImage: String? = null

}

@Type("content")
class LectureContent : BaseModel() {
    @JvmField
    var description: String? = null

    @JvmField
    @JsonProperty("content-id")
    var content_id: String? = null

    @JvmField
    var duration: Long? = null

    @JvmField
    var name: String? = null

    @JvmField
    var url: String? = null

    @JvmField
    @JsonProperty("cover-image")
    var coverImage: String? = null

}
@Type("document")
class LectureDocument : BaseModel() {
    @JvmField
    var description: String? = null

    @JvmField
    @JsonProperty("content-id")
    var content_id: String? = null

    @JvmField
    var duration: Long? = null

    @JvmField
    var name: String? = null

    @JvmField
    var url: String? = null

    @JvmField
    @JsonProperty("cover-image")
    var coverImage: String? = null

}

data class RatingModel(
        val rating: String,
        val count: Int,
        val stats: List<Double>,
        val userScore: Any?
)

data class User(val id: Int, val username: String, val firstname: String, val lastname: String, val photo: String, val email: String, val createdAt: String, val updatedAt: String)

