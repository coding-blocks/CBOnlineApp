package com.codingblocks.cbonlineapp.database.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    indices = [Index("crCourseId")],
    foreignKeys = [
        ForeignKey(
            entity = CourseModel::class,
            parentColumns = ["cid"],
            childColumns = ["crCourseId"],
            onDelete = ForeignKey.CASCADE)
    ]
)

data class RunModel(
    @PrimaryKey
    val crUid: String,
    val crName: String,
    var crDescription: String,
    var crEnrollmentStart: String,
    var crEnrollmentEnd: String,
    var crStart: String,
    var crEnd: String,
    var crPrice: String,
    var crMrp: String,
    var crUpdatedAt: String,
    var whatsappLink: String?,
    var totalContents: Int,
    var completionThreshold: Int,
    var goodiesThreshold: Int,
    var productId: Int,
    var crCourseId: String
) {
    constructor() : this("", "", "", "", "", "", "", "", "", "", "", 0, 0, 0, -1, "")
}
