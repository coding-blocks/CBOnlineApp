package com.codingblocks.onlineapi.models

import com.fasterxml.jackson.annotation.JsonProperty
import com.github.jasminb.jsonapi.Links
import com.github.jasminb.jsonapi.annotations.Id
import com.github.jasminb.jsonapi.annotations.Relationship
import com.github.jasminb.jsonapi.annotations.RelationshipLinks
import com.github.jasminb.jsonapi.annotations.Type
import com.google.gson.JsonObject

open class BaseModel {
    @Id
    var id: String = ""
    var updatedAt: String = ""
}


data class CourseFeatures(
    val icon: String,
    val text: String
)

@Type("projects")
data class Project(
    val title: String = "",
    val description: String = "",
    val image: String = ""
) : BaseModel()

@Type("courses")
data class Course(
    val title: String,
    val subtitle: String,
    val logo: String,
    val summary: String,
    val categoryId: Int,
    val promoVideo: String,
    val reviewCount: Int,
    val difficulty: String,
    val rating: Float,
    val slug: String?,
    val coverImage: String,
    val faq: String?,
    val coursefeatures: ArrayList<CourseFeatures>?,
    @Relationship("instructors")
    val instructors: ArrayList<Instructor>?,
    @Relationship("runs")
    val runs: ArrayList<Runs>?,
    @Relationship("projects", resolve = true)
    var projects: ArrayList<Project>?
) : BaseModel()

@Type("runs")
data class Runs(
    val name: String,
    val description: String,
    val start: String,
    val end: String,
    val price: String,
    val mrp: String?,
    val unlisted: Boolean,
    val enrollmentStart: String,
    val enrollmentEnd: String,
    @Relationship("sections")
    val sections: ArrayList<Sections>?,
    @Relationship("run-attempts")
    var runAttempts: ArrayList<RunAttempts>?,
    @Relationship("course")
    var course: Course?,
    @Relationship("ratings")
    var rating: ArrayList<Rating>?,
    val whatsappLink: String?,
    val productId: Int,
    val completionThreshold: Int,
    val goodiesThreshold: Int,
    //TODO ( Remove these values )
    val totalContents: Int = 100,
    val completedContents: Int = 50,
    @Relationship("tags")
    val tags: ArrayList<Tags>?

) : BaseModel()

//TODO ( change this to plural )
@Type("run_attempt")
data class RunAttempts(
    val certificateApproved: Boolean = false,
    val end: String = "",
    val premium: Boolean = false,
    val revoked: Boolean = false,
    val approvalRequested: Boolean = false,
    val doubtSupport: String? = "",
    @Relationship("run")
    val run: Runs? = null
) : BaseModel() {
    constructor(id: String) : this() {
        super.id = id
    }
}

//TODO ( change this to plural )
@Type("doubt")
data class Doubts(
    val body: String = "",
    val title: String = "",
    var status: String = "PENDING",
    val discourseTopicId: String = "",
    val conversationId: String? = null,
    @Relationship("run-attempt")
    val runAttempt: RunAttempts? = null,
    @Relationship("content")
    val content: ContentsId? = null,
    val createdAt: String = "",
    val categoryId: Int? = 0,
    val resolvedById: String? = null,
    val acknowledgedAt: String? = null,
    val resolvedAt: String? = null,
    val firebaseRef: String? = null,
    @Relationship("resolved-by")
    val resolvedBy: UserId? = null
) : BaseModel() {
    constructor(id: String, title: String,
                body: String,
                discourseTopicId: String,
                runAttempt: RunAttempts?,
                conversationId: String?,
                content: ContentsId?,
                status: String,
                createdAt: String
    ) : this(title, body, status, discourseTopicId, conversationId, runAttempt, content, createdAt) {
        super.id = id
    }
}

//TODO ( change this to plural )
@Type("content")
data class ContentsId(
    @Id
    val id: String?
) {
    var contentable: String? = null
    val duration: Long? = null
    val title: String? = null
}


@Type("comment")
class Comment(
    val body: String = "",
    val username: String = "",
    @Relationship("doubt")
    val doubt: Doubts? = null
) : BaseModel()


@Type("section")
data class Sections(
    var name: String?,
    var premium: Boolean? = false,
    var status: String? = null,
    var order: Int? = 0,
    @Relationship("contents")
    var contents: ArrayList<ContentsId>?,
    val runId: String? = "",
    @RelationshipLinks("contents")
    val courseContentLinks: Links? = null
) : BaseModel()

@Type("instructors")
data class Instructor(
    val name: String?,
    val description: String?,
    val photo: String?,
    val email: String?,
    val sub: String?
) : BaseModel()

class SectionContent(
    val order: Int,
    val sectionId: String?
) : BaseModel()


@Type("content")
class LectureContent(
    val contentable: String,
    val duration: Long?,
    val title: String,
    val sectionContent: SectionContent?,
    @Relationship("code-challenge")
    val codeChallenge: ContentCodeChallenge?,
    @Relationship("document")
    val document: ContentDocumentType?,
    @Relationship("lecture")
    val lecture: ContentLectureType?,
    @Relationship("progress")
    val progress: ContentProgress?,
    @Relationship("video")
    val video: ContentVideoType?,
    @Relationship("qna")
    val qna: ContentQna?,
    @Relationship("csv")
    val csv: ContentCsv?
) : BaseModel()

// =======Singular Models =========


// =======Section Content Models =========

@Type("code_challenge")
class ContentCodeChallenge() : BaseModel() {
    @JvmField
    var contentId: String? = null
    @JvmField
    var hbContestId: Int? = null
    @JvmField
    var name: String? = null
    @JvmField
    var hbProblemId: Int? = null
}

@Type("qna")
class ContentQna : BaseModel() {
    @JvmField
    var contentId: String? = null
    @JvmField
    var qId: Int? = null
    @JvmField
    var name: String? = null
}

@Type("csv")
class ContentCsv : BaseModel() {
    @JvmField
    var contentId: String? = null
    @JvmField
    var name: String? = null
    @JvmField
    var description: String? = null
    @JvmField
    var refCsv: String? = null
    @JvmField
    var datasetUrl: String? = null
    @JvmField
    var testcasesUrl: String? = null
    @JvmField
    var judgeScript: String? = null
}

@Type("document")
class ContentDocumentType : BaseModel() {
    @JvmField
    var contentId: String? = null
    @JvmField
    var duration: Long? = null
    @JvmField
    var name: String? = null
    @JvmField
    var markdown: String? = null
    @JvmField
    var pdfLink: String? = null
}

@Type("lecture")
class ContentLectureType : BaseModel() {
    @JvmField
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
    var videoId: String? = null
}

@Type("video")
class ContentVideoType : BaseModel() {
    @JvmField
    var description: String? = null
    @JvmField
    var contentId: String? = null
    @JvmField
    var duration: Long? = null
    @JvmField
    var name: String? = null
    @JvmField
    var url: String? = null
}

@Type("progress")
class ContentProgress(
    var contentId: String,
    var createdAt: String,
    var status: String,
    var runAttemptId: String) : BaseModel()

@Type("announcement")
class Announcement : BaseModel() {
    @JvmField
    var userId: String? = null
    @JvmField
    var createdAt: String? = null
    @JvmField
    var text: String? = null
    @JvmField
    var title: String? = null
    @JvmField
    var runId: String? = null
}

@Type("progresses")
class Progress : BaseModel() {
    @JvmField
    var status: String = "UNDONE"
    var runAttemptId: String = ""
    var contentId: String = ""
    @Relationship("run-attempt")
    @JvmField
    var runs: RunAttemptsId? = null
    @Relationship("content")
    @JvmField
    var content: ContentsId? = null
}

// =======Section Content Models =========


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
    var runAttempt: RunAttemptsId? = null
    @JvmField
    var submission: ArrayList<QuizSubmission> = arrayListOf()
}

class QuizSubmission : BaseModel() {
    @JvmField
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


@Type("note")
class Note(
    val duration: Double,
    @JsonProperty("createdAt")
    val createdAt: String,
    val deletedAt: String? = null,
    val text: String,
    @Relationship("run_attempt")
    val runAttempt: RunAttemptId? = null,
    @Relationship("content")
    val content: ContentId? = null
) : BaseModel()

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
    var runAttempt: RunAttemptsId? = null
    @Relationship("content")
    @JvmField
    var content: ContentsId? = null
}

@Type("run_attempt")
class RunAttemptId(
    @Id
    @JvmField
    val id: String?
)

@Type("users")
class UserId(
    @Id
    @JvmField
    val id: String?
)

@Type("user")
class User(
    val email: String,
    val firstname: String,
    val lastReadNotification: String,
    val lastname: String,
    val oneauthId: String,
    val photo: String?,
    val verifiedemail: String?,
    val verifiedmobile: String?,
    val roleId: Int = 0

) : BaseModel()

@Type("doubt_leaderboard")
class DoubtLeaderBoard(
    val ratingAll: Double,
    val ratingMonth: Double,
    val ratingWeek: Double,
    @Relationship("user")
    var user: User?
) : BaseModel()

@Type("run-attempts")
class RunAttemptsId(
    @Id
    val id: String?
)

@Type("content")
class ContentId(
    @Id
    val id: String?
) {
    val title: String? = null

}

@Type("rating")
class Rating : BaseModel()


@Type("tags")
class Tags : BaseModel() {
    @JvmField
    var name: String? = null
}

@Type("carousel_cards")
class CarouselCards(
    var title: String,
    var subtitle: String,
    var img: String,
    var buttonText: String,
    var buttonLink: String
) : BaseModel()

@Type("player")
class Player(
    var playerId: String? = null
)

@Type("jobs")
class Jobs(
    val coverImage: String?,
    val ctc: String,
    val deadline: String?,
    val description: String,
    val eligibility: String,
    val experience: String,
    val form: ArrayList<Form>?,
    val location: String,
    val postedOn: String,
    val type: String,
    val title: String,
    val accepting: Boolean = false,
    val eligible: Boolean = false,
    val status: String = "draft",
    @Relationship("company")
    val company: Company?,
    @Relationship("courses")
    val courses: ArrayList<CourseId>?,
    @Relationship("my-application")
    val application: ApplicationId?
) : BaseModel()

@Type("courses")
data class CourseId(
    @Id
    val id: String?
)

class Form(
    val name: String,
    val required: Boolean,
    val title: String,
    val type: String,
    val options: String?

)

@Type("companies")
class Company(
    val name: String?,
    val logo: String?,
    val description: String?,
    val website: String?,
    val inactive: Boolean = false,
    val contacts: ArrayList<Contact>?
) : BaseModel()

data class Contact(
    val email: String,
    val name: String,
    val phone: String
)

@Type("applications")
data class Applications(
    val extra: JsonObject,
    val resumeLink: String = "",
    @Relationship("job")
    val job: JobId
) : BaseModel()

@Type("jobs")
class JobId(
    @Id
    val id: String
)

@Type("applications")
class ApplicationId(
    @Id
    val id: String?
)




