package com.codingblocks.cbonlineapp.database.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.codingblocks.cbonlineapp.CBOnlineApp
import com.codingblocks.cbonlineapp.util.FileUtils
import com.codingblocks.onlineapi.models.CourseId
import java.sql.Date

@Entity
data class ContentLecture(
    var lectureUid: String = "",
    var lectureName: String = "",
    var lectureDuration: Long = 0L,
    var lectureId: String = "",
    var lectureContentId: String = "",
    var lectureUpdatedAt: String = "",
    var isDownloaded: Boolean = FileUtils.checkDownloadFileExists(CBOnlineApp.mInstance, lectureId),
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
        entity = ContentModel::class,
        parentColumns = ["ccid"],
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
        entity = ContentModel::class,
        parentColumns = ["ccid"],
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
    val courseId: ArrayList<CourseId>
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
