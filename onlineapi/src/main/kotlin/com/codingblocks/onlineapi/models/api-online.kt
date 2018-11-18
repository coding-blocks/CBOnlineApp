package com.codingblocks.onlineapi.models


import com.fasterxml.jackson.annotation.JsonProperty
import com.github.jasminb.jsonapi.annotations.Id
import com.github.jasminb.jsonapi.annotations.Relationship
import com.github.jasminb.jsonapi.annotations.Type

data class RatingModel(
        val rating: String,
        val count: Int,
        val stats: List<Double>,
        val userScore: Any?
)

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

@Type("instructor")
class InstructorCourse : BaseModel() {
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

    @JvmField
    var order: Int? = null

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

class SectionContent : BaseModel() {
    @JvmField
    val order: Int? = null
    @JvmField
    @JsonProperty("updated-at")
    val updatedAt: String? = null
    @JvmField
    @JsonProperty("content-id")
    val contentId: String? = null
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
    @JsonProperty("course-id")
    @JvmField
    var courseId: String? = null
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

    @Relationship("sections", resolve = true)
    @JvmField
    var sections: ArrayList<CourseSection>? = null


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


@Type("section")
class CourseSection : BaseModel() {

    @JvmField
    @JsonProperty("created-at")
    var createdAt: String? = null

    @JvmField
    var name: String? = null

    @JvmField
    var order: Int? = null

    @JvmField
    var premium: Boolean? = null

    @JvmField
    var status: String? = null

    @JvmField
    @JsonProperty("updated-at")
    var updatedAt: String? = null

    @JvmField
    @JsonProperty("run-id")
    var run_id: String? = null


    @Relationship("contents", resolve = true)
    @JvmField
    var contents: ArrayList<LectureContent>? = null

}

@Type("content")
class LectureContent : BaseModel() {

    @JvmField
    var contentable: String? = null

    @JvmField
    var duration: Long? = null

    @JvmField
    var title: String? = null

    @JvmField
    @JsonProperty("section-content")
    var section_content: SectionContent? = null


    //    @Relationship("code-challenge", resolve = true)
//    @JvmField
//    var code_challenge: LectureContent? = null
//
    @Relationship("document", resolve = true)
    @JvmField
    var document: ContentDocumentType? = null

    @Relationship("lecture", resolve = true)
    @JvmField
    var lecture: ContentLectureType? = null

    @Relationship("progress", resolve = true)
    @JvmField
    var progress: ContentProgress? = null

    @Relationship("video", resolve = true)
    @JvmField
    var video: ContentVideoType? = null

//    @Relationship("qna", resolve = true)
//    @JvmField
//    var qna: LectureContent? = null

}

@Type("document")
class ContentDocumentType : BaseModel() {

    @JvmField
    @JsonProperty("content-id")
    var content_id: String? = null

    @JvmField
    var duration: Long? = null

    @JvmField
    var name: String? = null

    @JvmField
    @JsonProperty("updated-at")
    val updatedAt: String? = null


    @JvmField
    var markdown: String? = null

    @JvmField
    @JsonProperty("pdf-link")
    var pdf_link: String? = null

}

@Type("lecture")
class ContentLectureType : BaseModel() {

    @JvmField
    @JsonProperty("created-at")
    var createdAt: String? = null

    @JvmField
    var description: String? = null

    @JvmField
    var name: String? = null

    @JvmField
    var duration: Long? = null

    @JvmField
    var status: String? = null

    @JvmField
    @JsonProperty("updated-at")
    var updatedAt: String? = null

    @JvmField
    @JsonProperty("video-url")
    var video_url: String? = null

}

@Type("video")
class ContentVideoType : BaseModel() {
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
    @JsonProperty("updated-at")
    var updatedAt: String? = null

}

@Type("progress")
class ContentProgress : BaseModel() {

    @JvmField
    @JsonProperty("content-id")
    var content_id: String? = null

    @JvmField
    @JsonProperty("created-at")
    var createdAt: String? = null

    @JvmField
    @JsonProperty("updated-at")
    var updatedAt: String? = null

    @JvmField
    var status: String? = null

    @JvmField
    @JsonProperty("run-attempt-id")
    var run_attempt_id: String? = null

}




