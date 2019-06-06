package com.codingblocks.cbonlineapp.database.models

import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.sql.Date

open class BaseModel(
    @NonNull
    @PrimaryKey
    var id: String,
    var updatedAt: String?
)

@Entity
data class Course(
    var uid: String,
    var title: String,
    var subtitle: String,
    var logo: String,
    var summary: String,
    var promoVideo: String,
    var difficulty: String,
    var reviewCount: Int,
    var rating: Float,
    var slug: String?,
    var coverImage: String,
    var updated_at: String?,
    var categoryId: Int

) : BaseModel(uid, updated_at)

@Entity(
    indices = [Index("crCourseId")],
    foreignKeys = [
        ForeignKey(
            entity = Course::class,
            parentColumns = ["id"],
            childColumns = ["crCourseId"]
        )
    ]
)
data class CourseRun(
    @PrimaryKey
    var crUid: String = "",
    var crAttemptId: String = "",
    var crName: String = "",
    var crDescription: String = "",
    var crEnrollmentStart: String = "",
    var crEnrollmentEnd: String = "",
    var crStart: String = "",
    var crEnd: String = "",
    var crPrice: String = "",
    var crMrp: String = "",
    var crCourseId: String = "",
    var crUpdatedAt: String = "",
    var progress: Double = 0.0,
    var title: String = "",
    var summary: String = "",
    var premium: Boolean = false,
    var hits: Int = 0,
    var recommended: Boolean = false
)

@Entity
data class Instructor(
    var uid: String,
    var name: String?,
    var description: String,
    var photo: String?,
    var updated_at: String?
) : BaseModel(uid, updated_at)

@Entity(
    primaryKeys = ["course_id", "instructor_id"],
    indices = [
        Index(value = ["course_id"]),
        Index(value = ["instructor_id"])
    ],
    foreignKeys = [
        ForeignKey(
            entity = Course::class,
            parentColumns = ["id"],
            childColumns = ["course_id"]
        ),
        ForeignKey(
            entity = Instructor::class,
            parentColumns = ["id"],
            childColumns = ["instructor_id"]
        )
    ]
)
data class CourseWithInstructor(
//        @Nullable @PrimaryKey(autoGenerate = true) val id: Int?,
    @ColumnInfo(name = "course_id") val courseId: String,
    @ColumnInfo(name = "instructor_id") val instructorId: String
)

@Entity(
    indices = [Index("run_id")],
    foreignKeys = [(ForeignKey(
        entity = CourseRun::class,
        parentColumns = ["crUid"],
        childColumns = ["run_id"],
        onDelete = ForeignKey.SET_NULL // or CASCADE
    ))]
)
data class CourseSection(
    var uid: String,
    var name: String,
    var order: Int,
    var premium: Boolean,
    var status: String,
    var run_id: String,
    var attempt_id: String,
    var updated_at: String
) : BaseModel(uid, updated_at)

@Entity(
    indices = [Index("section_id")],
    foreignKeys = [(ForeignKey(
        entity = CourseSection::class,
        parentColumns = ["id"],
        childColumns = ["section_id"],
        onDelete = ForeignKey.SET_NULL // or CASCADE
    ))]
)
data class CourseContent(
    var uid: String,
    var progress: String,
    var progressId: String,
    var title: String,
    var contentDuration: Long,
    var contentable: String,
    var order: Int,
    var section_id: String,
    var attempt_id: String,
    var premium: Boolean,
    var contentUpdatedAt: String,
    @Embedded
    @Nullable
    var contentLecture: ContentLecture = ContentLecture(),
    @Embedded
    @Nullable
    var contentDocument: ContentDocument = ContentDocument(),
    @Embedded
    @Nullable
    var contentVideo: ContentVideo = ContentVideo(),
    @Embedded
    @Nullable
    var contentQna: ContentQna = ContentQna(),
    @Embedded
    @Nullable
    var contentCode: ContentCodeChallenge = ContentCodeChallenge(),
    @Embedded
    @Nullable
    var contentCsv: ContentCsvModel = ContentCsvModel()
) : BaseModel(uid, contentUpdatedAt)

@Entity(
    primaryKeys = ["section_id", "content_id"],
    indices = [
        Index(value = ["section_id"]),
        Index(value = ["content_id"])
    ],
    foreignKeys = [
        ForeignKey(
            entity = CourseSection::class,
            parentColumns = ["id"],
            childColumns = ["section_id"]
        ),
        ForeignKey(
            entity = CourseContent::class,
            parentColumns = ["id"],
            childColumns = ["content_id"]
        )
    ]
)
data class SectionWithContent(
//        @Nullable @PrimaryKey(autoGenerate = true) val id: Int?,
    @ColumnInfo(name = "section_id") val sectionId: String,
    @ColumnInfo(name = "content_id") val contentId: String
)

@Entity
data class ContentLecture(
    var lectureUid: String = "",
    var lectureName: String = "",
    var lectureDuration: Long = 0L,
    var lectureId: String = "",
    var lectureContentId: String = "",
    var lectureUpdatedAt: String = "",
    var isDownloaded: String = "false",
    var date: Date = Date(0L)
)

@Entity
data class ContentDocument(
    var documentUid: String = "",
    var documentName: String = "",
    var documentPdfLink: String = "",
    var documentContentId: String = "",
    var documentUpdatedAt: String = ""
)

@Entity
data class ContentVideo(
    var videoUid: String = "",
    var videoName: String = "",
    var videoDuration: Long = 0L,
    var videoDescription: String? = "",
    var videoUrl: String = "",
    var videoContentId: String = "",
    var videoUpdatedAt: String = ""
)

@Entity
data class ContentCodeChallenge(
    var codeUid: String = "",
    var codeName: String = "",
    var codeProblemId: Int = 0,
    var codeContestId: Int = 0,
    var codeContentId: String = "",
    var codeUpdatedAt: String = ""
)

@Entity
data class ContentQna(
    var qnaUid: String = "",
    var qnaName: String = "",
    var qnaQid: Int = 0,
    var qnaContentId: String = "",
    var qnaUpdatedAt: String = ""
)

@Entity
data class ContentCsvModel(
    var csvUid: String = "",
    var csvName: String = "",
    var csvDescription: String = "",
    var csvContentId: String = "",
    var csvUpdatedAt: String = ""
)

@Entity(
    indices = [Index("contentId")],
    foreignKeys = [(ForeignKey(
        entity = CourseContent::class,
        parentColumns = ["id"],
        childColumns = ["contentId"],
        onDelete = ForeignKey.CASCADE // or CASCADE
    ))]
)
data class DoubtsModel(
    @PrimaryKey
    var dbtUid: String = "",
    var title: String = "",
    var body: String = "",
    var contentId: String = "",
    var status: String = "",
    var runAttemptId: String = "",
    var discourseTopicId: String = ""
)

@Entity(
    indices = [Index("contentId")],
    foreignKeys = [(ForeignKey(
        entity = CourseContent::class,
        parentColumns = ["id"],
        childColumns = ["contentId"],
        onDelete = ForeignKey.CASCADE // or CASCADE
    ))]
)
data class NotesModel(
    @PrimaryKey
    var nttUid: String = "",
    var duration: Double = 0.0,
    var text: String = "",
    var contentId: String = "",
    var runAttemptId: String = "",
    var createdAt: String = "",
    var deletedAt: String = ""
)
@Entity
data class Notification(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val heading: String,
    val body: String,
    val url: String,
    val seen: Boolean = false,
    val videoId: String = ""
)
