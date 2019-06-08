package com.codingblocks.onlineapi.models

import com.github.jasminb.jsonapi.Links
import com.github.jasminb.jsonapi.annotations.Id
import com.github.jasminb.jsonapi.annotations.Relationship
import com.github.jasminb.jsonapi.annotations.RelationshipLinks
import com.github.jasminb.jsonapi.annotations.Type

open class BaseModel {
    @Id
    @JvmField
    var id: String = ""
    @JvmField
    var updatedAt: String = ""
}

// =======Plurals Models =========
@Type("courses")
open class Course(
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
    @Relationship("instructors")
    val instructors: ArrayList<Instructor>?,
    @Relationship("runs")
    val runs: ArrayList<Runs>?
) : BaseModel()


@Type("run_attempts")
open class MyRunAttempts(
    val certificateApproved: Boolean,
    val end: String,
    val premium: Boolean,
    val revoked: Boolean
) : BaseModel()

@Type("runs")
open class Runs(
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
    @Relationship("tags")
    val tags: ArrayList<Tags>?
) : BaseModel()

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
    @Relationship("contents")
    @JvmField
    var contents: ArrayList<ContentsId>? = null
}

//=======Plurals Models =========

@Type("instructors")
open class Instructor(
    val name: String?,
    val description: String?,
    val photo: String?
) : BaseModel()

class SectionContent : BaseModel() {
    @JvmField
    val order: Int? = null
    @JvmField
    val sectionId: String? = null
}

// =======Singular Models =========


@Type("course")
class MyCourse(
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
    @Relationship("instructors")
    val instructors: ArrayList<Instructor>?,
    val coverImage: String
) : BaseModel()

@Type("run_attempt")
open class MyRunAttempt(
    val certificateApproved: Boolean,
    val end: String,
    val premium: Boolean,
    val revoked: Boolean,
    @Relationship("run")
    val run: MyCourseRuns?
) : BaseModel()

@Type("run")
class MyCourseRuns(
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
    val sections: ArrayList<CourseSection>?,
    @Relationship("run-attempts")
    var runAttempts: ArrayList<MyRunAttempts>?,
    @Relationship("course")
    var course: MyCourse?,
    @Relationship("ratings")
    var rating: ArrayList<Rating>?,
    val whatsappLink: String?
) : BaseModel()

@Type("section")
class CourseSection(
    val name: String,
    val status: String,
    val order: Int,
    val premium: Boolean,
    val runId: String,
    @RelationshipLinks("contents")
    val courseContentLinks: Links?,
    @Relationship("contents")
    val courseContent: ArrayList<LectureContent>? = null
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
class ContentProgress : BaseModel() {
    @JvmField
    var contentId: String? = null
    @JvmField
    var createdAt: String? = null
    @JvmField
    var status: String? = null
    @JvmField
    var runAttemptId: String? = null
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

@Type("progresses")
class Progress : BaseModel() {
    @JvmField
    var status: String? = null
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
    @JvmField
    var discourseTopicId: String = ""
    @JvmField
    var resolvedById: String = ""
    @Relationship("run-attempt", resolve = true)
    @JvmField
    var runAttempt: RunAttemptId? = null
    @Relationship("run-attempt", resolve = true)
    @JvmField
    var postrunAttempt: RunAttemptsId? = null
    @Relationship("content", resolve = true)
    @JvmField
    var content: ContentId? = null
    @Relationship("contents", resolve = true)
    @JvmField
    var contents: ContentsId? = null
}

@Type("comment")
class Comment : BaseModel() {
    @JvmField
    var body: String = ""
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
    var runAttempt: RunAttemptId? = null
    @Relationship("content", resolve = true)
    @JvmField
    var content: ContentId? = null
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

@Type("run-attempts")
class RunAttemptsId(
    @Id
    val id: String?
)

@Type("content")
class ContentId(
    @Id
    val id: String?
)

@Type("contents")
open class ContentsId(
    @Id
    val id: String?
) {
    var contentable: String? = null

    val duration: Long? = null
    val title: String? = null
    val sectionContent: SectionContent? = null
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

@Type("cricket_cup_matches")
class Match(
    var title: String? = "",
    var start: String? = "",
    var description: String? = "",
    var team1: Team? = null,
    var team2: Team? = null,
    var predictionEnd: String? = "",
    @Relationship("cricket-cup-questions")
    var cricketCupQuestions: ArrayList<CricketQuestion>? = null
) : BaseModel()

class Team(
    var id: String,
    var name: String,
    var sName: String,
    var flag: String,
    var logo: String
)

@Type("cricket_cup_questions")
class CricketQuestion(
    var title: String? = null,
    var description: String? = null,
    var score: String? = null,
    var correctChoiceId: String? = null,
    @Relationship("cricket-cup-choices")
    var cricketCupChoices: ArrayList<CricketChoice>? = null
) : BaseModel()

@Type("cricket_cup_choices")
class CricketChoice : BaseModel(){
    val content: String? = ""
}

@Type("cricket_cup_user_predictions")
class UserPrediction(
    @Relationship("cricket-cup-question")
    var cricketCupQuestion: CricketQuestion? = null,
    @Relationship("cricket-cup-choice")
    var choice: CricketChoice? = null,
    @Relationship("cricket-cup-match")
    var cricketCupMatch: Match? = null
) : BaseModel()

@Type("cricket-cup-user-predictions")
class UserPredictionPost(
    @Relationship("cricket-cup-question")
    var cricketCupQuestion: CricketQuestionPost,
    @Relationship("cricket-cup-choice")
    var choice: CricketChoicePost,
    @Relationship("cricket-cup-match")
    var cricketCupMatch: MatchPost
):BaseModel()
@Type("cricket-cup-questions")
class CricketQuestionPost(
    @Id
    val id:String
)
@Type("cricket-cup-choices")
class CricketChoicePost(
    @Id
    val id:String
)
@Type("cricket-cup-matches")
class MatchPost(
    @Id
    val id:String
)







