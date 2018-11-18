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
        var name: String,
        var description: String,
        var start: String,
        var end: String,
        var price: String,
        var mrp: String,
        var courseId: String,
        id: String
) : BaseModel(id)

@Entity(
        foreignKeys = [(ForeignKey(
                entity = CourseRun::class,
                parentColumns = ["id"],
                childColumns = ["run_id"],
                onDelete = ForeignKey.SET_NULL //or CASCADE
        ))]
)
class CourseSection(
        var name: String,
        var order: Int,
        var premium: Boolean,
        var status: String,
        var run_id: String,
        id: String
) : BaseModel(id)


@Entity(
        foreignKeys = [(ForeignKey(
                entity = CourseSection::class,
                parentColumns = ["id"],
                childColumns = ["section_id"],
                onDelete = ForeignKey.SET_NULL //or CASCADE
        ))]
)
class CourseContent(
        var progress: String,
        var title: String,
        var duration: Long,
        var contentable: String,
        var order: Int,
        var setion_id: String,
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





