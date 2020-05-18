package com.codingblocks.onlineapi.models

import com.github.jasminb.jsonapi.Links
import com.github.jasminb.jsonapi.RelType
import com.github.jasminb.jsonapi.annotations.Id
import com.github.jasminb.jsonapi.annotations.Relationship
import com.github.jasminb.jsonapi.annotations.RelationshipLinks
import com.github.jasminb.jsonapi.annotations.Type
import com.google.gson.JsonObject
import java.io.Serializable


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

@Type("courses", "course")
data class Course(
    val title: String = "",
    val subtitle: String = "",
    val logo: String = "",
    val summary: String = "",
    val categoryId: Int?,
    val promoVideo: String? = "",
    val reviewCount: Int = 0,
    val difficulty: String = "",
    val rating: Float = 0f,
    val slug: String? = "",
    val coverImage: String? = "",
    val faq: String?,
    val coursefeatures: ArrayList<CourseFeatures>?,
    @Relationship("instructors")
    val instructors: ArrayList<Instructor>?,
    @Relationship("runs")
    val runs: ArrayList<Runs>?,
    @Relationship("active-runs", "active_runs")
    val activeRuns: List<Runs>?,
    @Relationship("projects", resolve = true)
    var projects: ArrayList<Project>?,
    @Relationship("tags")
    val tags: ArrayList<Tags>?
) : BaseModel() {

    /** Logic to get [Runs] for enrolling into Trial */
    fun getTrialRun(tier: String): Runs? {
        return with(activeRuns ?: runs!!) {
            groupBy { it.tier }[tier]?.firstOrNull()
                ?: minBy { it.price }
        }
    }

    /** Logic to get [Runs] to display in [Sections] */
    fun getContentRun(tier: String): Runs? {
        return with(activeRuns ?: runs!!) {
            getTrialRun(tier)
                ?: groupBy { it.tier }["LIVE"]?.firstOrNull()
        }
    }
}

    @Type("runs", "run")
    data class Runs(
        val name: String = "",
        val description: String = "",
        val start: String = "",
        val end: String = "",
        val price: String = "",
        val mrp: String? = "",
        val unlisted: Boolean,
        val enrollmentStart: String = "",
        val enrollmentEnd: String = "",
        @Relationship("sections")
        val sections: ArrayList<Sections>?,
        @Relationship("run-attempts", "run_attempts")
        var runAttempts: ArrayList<RunAttempts>?,
        @Relationship("course")
        var course: Course?,
        @Relationship("ratings")
        var rating: ArrayList<Rating>?,
        val whatsappLink: String?,
        val productId: Int?,
        val completionThreshold: Int?,
        val goodiesThreshold: Int?,
        val totalContents: Int,
        val tier: String?
    ) : BaseModel() {

    }

    @Type("run-attempts", "run_attempts")
    data class RunAttempts(
        val certificateApproved: Boolean = false,
        val end: String = "",
        val premium: Boolean = false,
        val revoked: Boolean = false,
        val approvalRequested: Boolean = false,
        val doubtSupport: String? = "",
        val completedContents: Int = 0,
        val lastAccessedAt: String? = "",
        @Relationship("run")
        val run: Runs? = null,
        @Relationship("certificate")
        val certifcate: Certificate? = null,
        val runTier: String? = null
    ) : BaseModel() {
        constructor(id: String) : this() {
            super.id = id
        }
    }

    @Type("certificates")
    data class Certificate(val url: String?) : BaseModel()

    @Type("doubts", "doubt")
    data class Doubts(
        val body: String = "",
        val title: String = "",
        var status: String = "PENDING",
        val discourseTopicId: String = "",
        val conversationId: String? = null,
        @Relationship("run_attempt", "run-attempt")
        val runAttempt: RunAttempts? = null,
        @Relationship("content")
        val content: LectureContent? = null,
        val createdAt: String = "",
        val categoryId: Int? = 0,
        val resolvedById: String? = null,
        val acknowledgedAt: String? = null,
        val resolvedAt: String? = null,
        val firebaseRef: String? = null,
        @Relationship("resolved_by", "resolved-by")
        val resolvedBy: User? = null
    ) : BaseModel() {
        constructor(id: String, title: String,
                    body: String,
                    discourseTopicId: String,
                    runAttempt: RunAttempts?,
                    conversationId: String?,
                    content: LectureContent?,
                    status: String,
                    createdAt: String
        ) : this(title, body, status, discourseTopicId, conversationId, runAttempt, content, createdAt) {
            super.id = id
        }

        constructor(id: String?, title: String,
                    body: String,
                    runAttempt: RunAttempts?,
                    content: LectureContent?
        ) : this(title = title, body = body, runAttempt = runAttempt, content = content)

        constructor(id: String) : this() {
            super.id = id
        }
    }

    @Type("comments", "comment")
    data class Comment(
        val body: String = "",
        val username: String = "",
        val discourseTopicId: String = "",
        @Relationship("doubt")
        val doubt: Doubts? = null
    ) : BaseModel()


    @Type("sections")
    data class Sections(
        var name: String? = null,
        var premium: Boolean = false,
        var status: String? = null,
        var order: Int? = 0,
        @Relationship("contents", relType = RelType.RELATED)
        var contents: ArrayList<LectureContent>? = null,
        val runId: String? = "",
        @RelationshipLinks("contents")
        val courseContentLinks: Links? = null
    ) : BaseModel() {
        constructor(id: String) : this() {
            super.id = id
        }
    }

    @Type("contents")
    data class LectureContent(
        val contentable: String?,
        val duration: Long?,
        val title: String?,
        val sectionContent: SectionContent?,
        @Relationship("code_challenge", "code-challenge")
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
        val csv: ContentCsv?,
        @Relationship("bookmark")
        val bookmark: Bookmark?
    ) : BaseModel() {
        constructor(id: String)
            : this("", 0L, "", null, null, null, null, null, null, null, null, null) {
            super.id = id
        }
    }

    @Type("instructors")
    data class Instructor(
        val name: String?,
        val description: String?,
        val photo: String?,
        val email: String?,
        val sub: String?
    ) : BaseModel()

    @Type("progresses", "progress")
    data class ContentProgress(
        @Id
        val id: String? = null,
        val updatedAt: String? = null,
        val contentId: String? = null,
        val createdAt: String? = null,
        val status: String,
        val runAttemptId: String? = null,
        @Relationship("run_attempt", "run-attempt")
        val runAttempt: RunAttempts? = null,
        @Relationship("content")
        val content: LectureContent? = null
    ) {
        constructor(status: String, runAttemptId: RunAttempts, contentId: LectureContent, progressId: String?)
            : this(status = status, runAttempt = runAttemptId, content = contentId, id = progressId)
    }

    @Type("bookmarks")
    data class Bookmark(
        @Id
        val id: String?,
        @Relationship("run-attempt")
        val runAttempt: RunAttempts? = null,
        @Relationship("content")
        val content: LectureContent? = null,
        @Relationship("section")
        val section: Sections? = null,
        val createdAt: String? = null,
        val runAttemptId: String? = null,
        val sectionId: String? = null,
        val contentId: String? = null
    ) {
        constructor(runAttemptId: RunAttempts, contentId: LectureContent, sectionId: Sections)
            : this(null, runAttemptId, contentId, sectionId)
    }

    @Type("quiz-attempts", "quiz_attempts")
    data class QuizAttempt(
        val createdAt: String? = null,
        var result: QuizResult? = null,
        val status: String? = "DRAFT",
        @Relationship("qna")
        @JvmField
        var qna: ContentQna? = null,
        @Relationship("run-attempt")
        var runAttempt: RunAttempts? = null,
        var submission: ArrayList<QuizSubmission>? = null
    ) : BaseModel() {
        constructor(qnaId: ContentQna, runAttemptId: RunAttempts)
            : this(qna = qnaId, runAttempt = runAttemptId)

        constructor(id: String, qnaId: ContentQna)
            : this(qna = qnaId) {
            super.id = id

        }
    }

    @Type("notes")
    data class Note(
        val duration: Double,
        val createdAt: String? = null,
        val deletedAt: String? = null,
        val text: String,
        @Relationship("run-attempt", "run_attempt")
        val runAttempt: RunAttempts? = null,
        @Relationship("content")
        val content: LectureContent? = null
    ) : BaseModel(), Serializable {
        constructor(id: String, duration: Double, text: String, runAttemptId: RunAttempts, contentId: LectureContent)
            : this(duration, null, null, text, runAttemptId, contentId) {
            super.id = id
        }

        constructor(duration: Double, text: String, runAttemptId: RunAttempts, contentId: LectureContent)
            : this(duration, null, null, text, runAttemptId, contentId)

    }

    @Type("users", "user")
    data class User(
        val email: String?,
        val firstname: String,
        val lastReadNotification: String?,
        val lastname: String,
        val oneauthId: String?,
        val photo: String?,
        val verifiedemail: String?,
        val verifiedmobile: String?,
        val username: String = "",
        val roleId: Int = 0,
        val graduationyear: String? = "",
        val college: String? = "",
        val mobile: String? = "",
        val branch: String? = ""
    ) : BaseModel()


    class SectionContent(
        val order: Int,
        val sectionId: String?
    ) : BaseModel()


// =======Section Content Models =========

    @Type("code-challenges", "code_challenges")
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

    @Type("qnas", "qna")
    class ContentQna(
        var contentId: String? = null,
        var qId: Int? = null,
        var name: String? = null
    ) : BaseModel() {
        constructor(id: String)
            : this() {
            super.id = id
        }
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

    @Type("documents", "document")
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

    @Type("lectures", "lecture")
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

    @Type("videos", "video")
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

// =======Section Content Models =========


    @Type("quizzes")
    class Quizzes(
        var title: String? = null,
        var description: String? = null,
        @Relationship("questions")
        var questions: ArrayList<Question>? = null
    ) : BaseModel()

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

    @Type("doubt_leaderboard")
    class DoubtLeaderBoard(
        val ratingAll: Double,
        val ratingMonth: Double,
        val ratingWeek: Double,
        @Relationship("user")
        var user: User?
    ) : BaseModel()


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

    @Type("career_tracks")
    data class CareerTracks(
        var name: String = "",
        var slug: String = "",
        var description: String? = "",
        var unlisted: Boolean,
        var logo: String = "",
        var background: String = "",
        var status: String? = "",
        val languages: List<String>,
        @Relationship("courses", relType = RelType.RELATED)
        var courses: List<Course>?,
        @Relationship("professions")
        var professions: List<Professions>?,
        @RelationshipLinks("courses")
        val coursesLinks: Links? = null
    ) : BaseModel()

    @Type("professions")
    class Professions(
        val title: String = ""
    ) : BaseModel()

    @Type("players")
    data class Player(
        @Id
        var id: String? = null,
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
        val courses: ArrayList<Course>?,
        @Relationship("my_application", "my-application")
        val application: ApplicationId?
    ) : BaseModel()

    class Form(
        val name: String,
        val required: Boolean,
        val title: String = "",
        val type: String = "",
        val options: String = ""

    )

    @Type("companies")
    class Company(
        val name: String = "",
        val logo: String = "",
        val description: String = "",
        val website: String = "",
        val inactive: Boolean = false,
        val contacts: ArrayList<Contact>?
    ) : BaseModel()

    data class Contact(
        val email: String = "",
        val name: String = "",
        val phone: String = ""
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




