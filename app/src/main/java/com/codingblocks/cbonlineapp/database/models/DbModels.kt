package com.codingblocks.cbonlineapp.database.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.codingblocks.cbonlineapp.CBOnlineApp
import com.codingblocks.cbonlineapp.util.FileUtils
import com.codingblocks.onlineapi.models.Course
import java.sql.Date

@Entity
data class ContentLecture(
    var lectureUid: String = "",
    var lectureName: String = "",
    var lectureDuration: Long = 0L,
    var lectureId: String = "",
    var lectureSectionId: String = "",
    var lectureUpdatedAt: String = "",
    var isDownloaded: Boolean = if (lectureId.isEmpty()) false else FileUtils.checkDownloadFileExists(CBOnlineApp.mInstance, lectureId),
    var date: Date = Date(0L),
    var lectureContentId: String = ""
) : BaseModel()

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
data class ContentQnaModel(
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
//    indices = [Index("contentId")],
//    foreignKeys = [(ForeignKey(
//        entity = ContentModel::class,
//        parentColumns = ["ccid"],
//        childColumns = ["contentId"],
//        onDelete = ForeignKey.CASCADE
//    ))]
)
data class NotesModel(
    @PrimaryKey
    var nttUid: String = "",
    var duration: Double = 0.0,
    var text: String = "",
    var contentId: String = "",
    var runAttemptId: String = "",
    var createdAt: String = "",
    var deletedAt: String? = "",
    val contentTitle: String = ""
) : BaseModel()

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

@Entity(
    indices = [Index("contentId")],
    foreignKeys = [(ForeignKey(
        entity = ContentModel::class,
        parentColumns = ["ccid"],
        childColumns = ["contentId"],
        onDelete = ForeignKey.CASCADE
    ))]
)
data class BookmarkModel(
    @PrimaryKey
    var bookmarkUid: String = "",
    var runAttemptId: String = "",
    var contentId: String = "",
    var sectionId: String = "",
    var createdAt: String = "",
    var sectionName: String = "",
    var contentName: String = ""
) : BaseModel()

@Entity
data class JobsModel(
    @PrimaryKey
    val uid: String,
    val coverImage: String?,
    val ctc: String,
    val deadline: String?,
    val description: String,
    val eligibility: String,
    val experience: String,
    val location: String,
    val postedOn: String,
    val type: String,
    val title: String,
    @Embedded
    val company: Companies,
    val courseId: ArrayList<Course>
)

class FormModel(
    val name: String,
    val required: Boolean,
    val title: String,
    val type: String
)

@Entity
data class Companies(
    val id: String,
    val name: String,
    val logo: String,
    val companyDescription: String,
    val website: String
)

open class BaseModel()

enum class LibraryTypes {
    NOTE, NOTESVIDEO, BOOKMARK, DOWNLOADS
}
