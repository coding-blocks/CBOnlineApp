package com.codingblocks.cbonlineapp.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

open class BaseModel(
        @PrimaryKey
        var id: String? = null)

@Entity(tableName = "courseData")
class MyCourseRuns : BaseModel() {
    @JvmField
    var name: String? = null
    @JvmField
    var description: String? = null
    @JvmField
    var start: String? = null
    @JvmField
    var end: String? = null
    @JvmField
    var price: String? = null
    @JvmField
    var mrp: String? = null
    @JvmField
    val enrollmentStart: String? = null
    @JvmField
    val enrollmentEnd: String? = null
    @JvmField
    var course: MyCourse? = null

}

class MyCourse : BaseModel() {
    @JvmField
    var title: String? = null
    @JvmField
    var subtitle: String? = null
    @JvmField
    var logo: String? = null
    @JvmField
    var summary: String? = null

    @JvmField
    var categoryName: String? = null

    @JvmField
    var categoryId: Int? = null

    @JvmField
    var promoVideo: String? = null

    @JvmField
    var reviewCount: Int? = null

    @JvmField
    var difficulty: String? = null

    @JvmField
    var rating: Float? = null

    @JvmField
    var slug: String? = null

    @JvmField
    var coverImage: String? = null


}

@Entity(
        foreignKeys = [(ForeignKey(
                entity = MyCourseRuns::class,
                parentColumns = ["id"],
                childColumns = ["run_id"],
                onDelete = ForeignKey.SET_NULL //or CASCADE
        ))]
)
class CourseSection : BaseModel() {

    @JvmField
    var createdAt: String? = null

    @JvmField
    var name: String? = null

    @JvmField
    var order: Int? = null

    @JvmField
    var premium: Boolean? = null

    @JvmField
    var status: String? = null

    @JvmField
    var updatedAt: String? = null

    @JvmField
    var run_id: String? = null


}

@Entity(
        foreignKeys = [(ForeignKey(
                entity = MyCourseRuns::class,
                parentColumns = ["id"],
                childColumns = ["content_id"],
                onDelete = ForeignKey.SET_NULL //or CASCADE
        ))]
)
data class Lecture(@PrimaryKey var id: String,
                   var updatedAt: String,
                   var name: String,
                   var duration: Long,
                   var url: String,
                   var content_id: String

) {
    constructor() : this("", "", "", 0, "", "")

}

@Entity(
        foreignKeys = [(ForeignKey(
                entity = MyCourseRuns::class,
                parentColumns = ["id"],
                childColumns = ["content_id"],
                onDelete = ForeignKey.SET_NULL //or CASCADE
        ))]
)
data class Video(@PrimaryKey var id: String,
                 var updatedAt: String,
                 var name: String,
                 var duration: Long,
                 var description: String,
                 var url: String,
                 var content_id: String
) {
    constructor() : this("", "", "", 0, "", "", "")

}

@Entity(
        foreignKeys = [(ForeignKey(
                entity = MyCourseRuns::class,
                parentColumns = ["id"],
                childColumns = ["content_id"],
                onDelete = ForeignKey.SET_NULL //or CASCADE
        ))]
)
data class Document(@PrimaryKey var id: String,
                    var updatedAt: String,
                    var name: String,
                    var pdf_link: String,
                    var content_id: String

) {
    constructor() : this("", "", "", "", "")

}





