package com.codingblocks.onlineapi.models


import com.fasterxml.jackson.annotation.JsonProperty
import com.github.jasminb.jsonapi.Links
import com.github.jasminb.jsonapi.annotations.Id
import com.github.jasminb.jsonapi.annotations.Relationship
import com.github.jasminb.jsonapi.annotations.RelationshipLinks
import com.github.jasminb.jsonapi.annotations.Type


open class BaseModel {
    @Id
    @JvmField
    var id: String? = null
    @JvmField
    @JsonProperty("updated-at")
    var updatedAt: String? = null
}

@Type("instructors")
open class Instructor : BaseModel() {
    @JvmField
    var name: String? = null
    @JvmField
    var description: String? = null
    @JvmField
    var photo: String? = null

    @Relationship("courses", resolve = true)
    @JvmField
    var courses: ArrayList<Course>? = null

    @JsonProperty("instructor-course")
    @JvmField
    var instructorCourse: InstructorCourse? = null


}

@Type("instructor")
class InstructorSingle : Instructor()

class InstructorCourse : BaseModel() {
    @JvmField
    @JsonProperty("course-id")
    var courseId: String? = null
}

@Type("run_attempts")
open class MyRunAttempts : BaseModel() {

    @JvmField
    @JsonProperty("certificate-approved")
    var certificateApproved: Boolean? = false
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
class MyRunAttempt : MyRunAttempts()

@Type("runs")
open class Runs : BaseModel() {
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
    var unlisted: Boolean? = null
    @JvmField
    @JsonProperty("enrollment-start")
    val enrollmentStart: String? = null
    @JvmField
    @JsonProperty("enrollment-end")
    val enrollmentEnd: String? = null
    @Relationship("sections", resolve = true)
    @JvmField
    var sections: ArrayList<Sections>? = null
    @Relationship("tags", resolve = true)
    @JvmField
    var tags: ArrayList<Tags>? = null
    @Relationship("certificate", resolve = true)
    @JvmField
    var certificate: Certificate? = null

}

@Type("certificate")
class Certificate : BaseModel(){

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
    var runAttempts: ArrayList<MyRunAttempts>? = null

    @Relationship("sections", resolve = true)
    @JvmField
    var sections: ArrayList<CourseSection>? = null

    @Relationship("ratings", resolve = true)
    @JvmField
    var rating: ArrayList<Rating>? = null

    @Relationship("announcements", resolve = true)
    @JvmField
    var announcements: ArrayList<Announcement>? = null

    @JvmField
    @JsonProperty("whatsapp-link")
    var whatsappLink: String? = null


}
@Type("rating")
class Rating : BaseModel()

@Type("courses")
open class Course : BaseModel() {
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
    var categoryId: Int = 0

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

@Type("tags")
class Tags : BaseModel(){
    @JvmField
    var name: String? = null
}

@Type("course")
class MyCourse : Course()

@Type("sections")
open class Sections : BaseModel() {
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
open class Contents : BaseModel() {
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
    @JsonProperty("section-id")
    val sectionId: String? = null
}


@Type("section")
class CourseSection : BaseModel() {

    @JvmField
    var name: String? = null

    @JvmField
    var preminum: Boolean? = false
    @JvmField
    var status: String? = null

    @JvmField
    var order: Int? = null

    @JvmField
    @JsonProperty("created-at")
    var createdAt: String? = null

    @JvmField
    var premium: Boolean? = null


    @JvmField
    @JsonProperty("run-id")
    var run_id: String? = null

    @RelationshipLinks("contents")
    @JvmField
    var courseContentLinks: Links? = null

    @Relationship("contents", resolve = true)
    @JvmField
    var courseContent: ArrayList<LectureContent>? = null



}

@Type("content")
class LectureContent : Contents() {

    @JvmField
    @JsonProperty("section-content")
    var section_content: SectionContent? = null


    @Relationship("code-challenge", resolve = true)
    @JvmField
    var code_challenge: ContentCodeChallenge? = null

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

    @Relationship("qna", resolve = true)
    @JvmField
    var qna: ContentQna? = null

    @Relationship("csv", resolve = true)
    @JvmField
    var csv: ContentCsv? = null

}

@Type("code_challenge")
class ContentCodeChallenge : BaseModel() {
    @JvmField
    @JsonProperty("content-id")
    var content_id: String? = null

    @JvmField
    @JsonProperty("hb-contest-id")
    var hb_contest_id: Int? = null

    @JvmField
    var name: String? = null


    @JvmField
    @JsonProperty("hb-problem-id")
    var hb_problem_id: Int? = null

}

@Type("qna")
class ContentQna : BaseModel() {
    @JvmField
    @JsonProperty("content-id")
    var content_id: String? = null

    @JvmField
    @JsonProperty("q-id")
    var q_id: Int? = null

    @JvmField
    var name: String? = null
}

@Type("csv")
class ContentCsv : BaseModel() {
    @JvmField
    @JsonProperty("content-id")
    var content_id: String? = null
    @JvmField
    var name: String? = null
    @JvmField
    var description: String? = null
    @JvmField
    @JsonProperty("ref-csv")
    var ref_csv: String? = null
    @JvmField
    @JsonProperty("dataset-url")
    var dataset_url: String? = null
    @JvmField
    @JsonProperty("testcases-url")
    var testcases_url: String? = null
    @JvmField
    @JsonProperty("judge-script")
    var judge_script: String? = null
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
    var status: String? = null

    @JvmField
    @JsonProperty("run-attempt-id")
    var run_attempt_id: String? = null

}

@Type("announcement")
class Announcement : BaseModel() {

    @JvmField
    @JsonProperty("user-id")
    var user_id: String? = null

    @JvmField
    @JsonProperty("created-at")
    var createdAt: String? = null


    @JvmField
    var text: String? = null

    @JvmField
    var title: String? = null

    @JvmField
    @JsonProperty("run-id")
    var run_id: String? = null

}

@Type("progresses")
class Progress : BaseModel() {
    @JvmField
    @JsonProperty("status")
    var status: String? = null

    @Relationship("run-attempt")
    @JvmField
    var runs: RunAttemptsModel? = null

    @Relationship("content")
    @JvmField
    var content: Contents? = null

}

@Type("quizzes")
class Quizzes : BaseModel() {
    @JvmField
    var title: String? = null

    @JvmField
    var description: String? = null

    @Relationship("questions", resolve = true)
    @JvmField
    var questions: ArrayList<Question>? = null

}

@Type("questions")
class Question : BaseModel() {
    @JvmField
    var title: String? = null

    @JvmField
    var description: String? = null

    @Relationship("choices", resolve = true)
    @JvmField
    var choices: ArrayList<Choice>? = null
}

@Type("choices")
class Choice : BaseModel() {
    @JvmField
    var title: String? = null

    @JvmField
    var description: String? = null

    @JvmField
    var marked: Boolean = false

    @JvmField
    var correct: Boolean? = null
}

@Type("quiz_attempts")
class QuizAttempt : BaseModel() {

    @JsonProperty("created-at")
    @JvmField
    var createdAt: String? = null

    @JvmField
    var result: QuizResult? = null

    @JvmField
    var status: String? = "DRAFT"

    @Relationship("qna", resolve = true)
    @JvmField
    var qna: Quizqnas? = null

    @Relationship("run-attempt", resolve = true)
    @JvmField
    var runAttempt: RunAttemptsModel? = null

    @JvmField
    var submission: ArrayList<QuizSubmission> = arrayListOf()

}

class QuizSubmission : BaseModel() {

    @JvmField
    @JsonProperty("marked-choices")
    var markedChoices: Array<String>? = null
}


class QuizResult : BaseModel() {
    @JvmField
    var type: String? = null
    @JvmField
    var score: Int? = null

    @JvmField
    var questions: ArrayList<QuizQuestion>? = null

}

class QuizQuestion : BaseModel() {
    @JvmField
    var score: Int? = null

    @JvmField
    var answers: Array<String>? = null

    @JvmField
    var correctlyAnswered: Array<Choice>? = null

    @JvmField
    var incorrectlyAnswered: Array<Choice>? = null


}

@Type("qnas")
class Quizqnas : BaseModel()

@Type("run-attempts")
class RunAttemptsModel : BaseModel()

@Type("doubt")
class DoubtsJsonApi : BaseModel() {
    @JvmField
    var category: Int? = null

    @JvmField
    var body: String = ""

    @JvmField
    var title: String = ""

    @JvmField
    var status: String = "PENDING"

    @JsonProperty("discourse-topic-id")
    @JvmField
    var discourseTopicId: String = ""

    @JsonProperty("resolved-by-id")
    @JvmField
    var resolvedById: String = ""

    @Relationship("run-attempt", resolve = true)
    @JvmField
    var runAttempt: MyRunAttempt? = null

    @Relationship("run-attempt", resolve = true)
    @JvmField
    var postrunAttempt: RunAttemptsModel? = null

////    @Relationship("comments", resolve = true,relType = RelType.RELATED)
//    @JvmField
//    var comments: List<Comments>? = null

    @Relationship("content", resolve = true)
    @JvmField
    var content: Contents? = null

}

@Type("comment")
class Comment : BaseModel() {
    @JvmField
    var body: String = ""

    @JsonProperty("discourse-topic-id")
    @JvmField
    var discourseTopicId: String = ""

    @JvmField
    var username: String = ""

    @Relationship("doubt", resolve = true)
    @JvmField
    var doubt: DoubtsJsonApi? = null

}

@Type("note")
class Note : BaseModel() {
    @JvmField
    var duration: Double? = null

    @JvmField
    var text: String? = null

    @JvmField
    var createdAt: String? = null

    @JvmField
    var deletedAt: String? = null

    @Relationship("run_attempt", resolve = true)
    @JvmField
    var runAttempt: MyRunAttempt? = null

    @Relationship("content", resolve = true)
    @JvmField
    var content: Contents? = null

}

@Type("notes")
class Notes : BaseModel() {
    @JvmField
    var duration: Double? = null

    @JvmField
    var text: String? = null

    @JvmField
    var createdAt: String? = null

    @JvmField
    var deletedAt: String? = null

    @Relationship("run-attempt", resolve = true)
    @JvmField
    var runAttempt: RunAttemptsModel? = null

    @Relationship("content", resolve = true)
    @JvmField
    var content: Contents? = null

}

@Type("carousel_cards")
class CarouselCards : BaseModel() {
    @JvmField
    var title: String? = null

    @JvmField
    var subtitle: String? = null

    @JvmField
    var img: String? = null

    @JsonProperty("button-text")
    @JvmField
    var buttonText: String? = null

    @JsonProperty("button-link")
    @JvmField
    var buttonLink: String? = null


}







