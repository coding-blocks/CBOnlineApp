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
open class RunModel(
    @PrimaryKey
    var crUid: String = "",
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
    var whatsappLink: String? = "",
    var crAttemptId: String? = null,
    var premium: Boolean = false,
    var crRunAttemptEnd: String = "",
    var approvalRequested: Boolean = false,
    var certificateApproved: Boolean = false,
    var totalContents: Int = 0,
    var completedContents: Int = 0,
    var progress: Double = 0.0,
    var completionThreshold: Int = 0,
    var goodiesThreshold: Int = 0,
    var productId: Int = 0,
    var recommended: Boolean = false,
    var hits: Int = 0
)
