package com.codingblocks.cbonlineapp.database

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

open class BaseModel(
        @NonNull

        var updatedAt: String = ""
)


@Entity
data class CourseRun(
        @PrimaryKey
        var id: String,
        var attempt_id: String,
        var name: String,
        var description: String,
        var start: String,
        var end: String,
        var price: String,
        var mrp: String,
        var courseId: String
) : BaseModel()

@Entity(
        foreignKeys = [(ForeignKey(
                entity = CourseRun::class,
                parentColumns = ["id"],
                childColumns = ["run_id"],
                onDelete = ForeignKey.SET_NULL //or CASCADE
        ))]
)
data class CourseSection(
        @PrimaryKey
        var id: String,
        var name: String,
        var order: Int,
        var premium: Boolean,
        var status: String,
        var run_id: String
) : BaseModel()


@Entity(
        foreignKeys = [(ForeignKey(
                entity = CourseSection::class,
                parentColumns = ["id"],
                childColumns = ["section_id"],
                onDelete = ForeignKey.SET_NULL //or CASCADE
        ))]
)
data class CourseContent(
        @PrimaryKey
        var id: String,
        var progress: String,
        var title: String,
        var duration: Long,
        var contentable: String,
        var order: Int,
        var section_id: String
) : BaseModel()

@Entity(
        foreignKeys = [(ForeignKey(
                entity = CourseContent::class,
                parentColumns = ["id"],
                childColumns = ["content_id"],
                onDelete = ForeignKey.SET_NULL //or CASCADE
        ))]
)
class ContentLecture(
        var name: String,
        var duration: Long,
        var url: String,
        var content_id: String,
        id: String
) : BaseModel(id)

@Entity(
        foreignKeys = [(ForeignKey(
                entity = CourseContent::class,
                parentColumns = ["id"],
                childColumns = ["content_id"],
                onDelete = ForeignKey.SET_NULL //or CASCADE
        ))]
)
class ContentVideo(
        var name: String,
        var duration: Long,
        var description: String,
        var url: String,
        var content_id: String,
        id: String
) : BaseModel(id)

@Entity(
        foreignKeys = [(ForeignKey(
                entity = CourseContent::class,
                parentColumns = ["id"],
                childColumns = ["content_id"],
                onDelete = ForeignKey.SET_NULL //or CASCADE
        ))]
)
class ContentDocument(
        var name: String,
        var pdf_link: String,
        var content_id: String,
        id: String
) : BaseModel(id)





