package com.codingblocks.cbonlineapp.database

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

open class BaseModel(
        @NonNull
        @PrimaryKey
        var id: String = "",
        var updatedAt: String = ""
)


@Entity
class CourseRun(
        var name: String = "",
        var description: String = "",
        var start: String = "",
        var end: String = "",
        var price: String = "",
        var mrp: String = "",
        var courseId: String = ""
) : BaseModel()

@Entity(
        foreignKeys = [(ForeignKey(
                entity = CourseRun::class,
                parentColumns = ["id"],
                childColumns = ["run_id"],
                onDelete = ForeignKey.SET_NULL //or CASCADE
        ))]
)
class CourseSection(
        var name: String = "",
        var order: Int = 0,
        var premium: Boolean = false,
        var status: String = "",
        var run_id: String = "") : BaseModel()


@Entity
class CourseContent(
        var progress: String = "UNDONE",
        var title: String = "",
        var duration: Long = 0,
        var contentable: String = "",
        var order: Int = 0
) : BaseModel()

@Entity(
        foreignKeys = [(ForeignKey(
                entity = CourseContent::class,
                parentColumns = ["id"],
                childColumns = ["content_id"],
                onDelete = ForeignKey.SET_NULL //or CASCADE
        ))]
)
data class ContentLecture(
        var name: String = "",
        var duration: Long = 0,
        var url: String = "",
        var content_id: String = ""

) : BaseModel()

@Entity(
        foreignKeys = [(ForeignKey(
                entity = CourseContent::class,
                parentColumns = ["id"],
                childColumns = ["content_id"],
                onDelete = ForeignKey.SET_NULL //or CASCADE
        ))]
)
data class ContentVideo(
        var name: String = "",
        var duration: Long = 0,
        var description: String = "",
        var url: String = "",
        var content_id: String = ""
) : BaseModel()

@Entity(
        foreignKeys = [(ForeignKey(
                entity = CourseContent::class,
                parentColumns = ["id"],
                childColumns = ["content_id"],
                onDelete = ForeignKey.SET_NULL //or CASCADE
        ))]
)
data class ContentDocument(
        var name: String = "",
        var pdf_link: String = "",
        var content_id: String = ""

) : BaseModel()





