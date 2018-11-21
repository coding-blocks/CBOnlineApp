package com.codingblocks.cbonlineapp.database

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

open class BaseModel(
        @NonNull
        @PrimaryKey
        var id: String,
        var updatedAt: String
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
        var slug: String,
        var coverImage: String,
        var updated_at: String
) : BaseModel(uid, updated_at)

@Entity
data class CourseRun(
        var uid: String,
        var attempt_id: String,
        var name: String,
        var description: String,
        var start: String,
        var end: String,
        var price: String,
        var mrp: String,
        var courseId: String,
        var updated_at: String
) : BaseModel(uid, updated_at)

@Entity(
        foreignKeys = [(ForeignKey(
                entity = Course::class,
                parentColumns = ["id"],
                childColumns = ["course_id"],
                onDelete = ForeignKey.SET_NULL //or CASCADE
        ))]
)
data class Instructor(
        var uid: String,
        var name: String,
        var description: String,
        var photo: String,
        var updated_at: String,
        var course_id: String
) : BaseModel(uid, updated_at)

@Entity(
        foreignKeys = [(ForeignKey(
                entity = CourseRun::class,
                parentColumns = ["id"],
                childColumns = ["run_id"],
                onDelete = ForeignKey.SET_NULL //or CASCADE
        ))]
)
data class CourseSection(
        var uid: String,
        var name: String,
        var order: Int,
        var premium: Boolean,
        var status: String,
        var run_id: String,
        var updated_at: String
) : BaseModel(uid, updated_at)


@Entity(
        foreignKeys = [(ForeignKey(
                entity = CourseSection::class,
                parentColumns = ["id"],
                childColumns = ["section_id"],
                onDelete = ForeignKey.SET_NULL //or CASCADE
        ))]
)
data class CourseContent(
        var uid: String,
        var progress: String,
        var title: String,
        var duration: Long,
        var contentable: String,
        var order: Int,
        var section_id: String,
        var updated_at: String
) : BaseModel(uid, updated_at)

@Entity(
        foreignKeys = [(ForeignKey(
                entity = CourseContent::class,
                parentColumns = ["id"],
                childColumns = ["content_id"],
                onDelete = ForeignKey.SET_NULL //or CASCADE
        ))]
)
data class ContentLecture(
        var uid: String,
        var name: String,
        var duration: Long,
        var url: String,
        var content_id: String,
        var updated_at: String

) : BaseModel(uid, updated_at)

@Entity(
        foreignKeys = [(ForeignKey(
                entity = CourseContent::class,
                parentColumns = ["id"],
                childColumns = ["content_id"],
                onDelete = ForeignKey.SET_NULL //or CASCADE
        ))]
)
data class ContentVideo(
        var uid: String,
        var name: String,
        var duration: Long,
        var description: String,
        var url: String,
        var content_id: String,
        var updated_at: String
) : BaseModel(uid, updated_at)

@Entity(
        foreignKeys = [(ForeignKey(
                entity = CourseContent::class,
                parentColumns = ["id"],
                childColumns = ["content_id"],
                onDelete = ForeignKey.SET_NULL //or CASCADE
        ))]
)
data class ContentDocument(
        var uid: String,
        var name: String,
        var pdf_link: String,
        var content_id: String,
        var updated_at: String
) : BaseModel(uid, updated_at)







