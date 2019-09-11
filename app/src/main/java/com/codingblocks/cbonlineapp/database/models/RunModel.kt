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
            childColumns = ["crCourseId"]
        )
    ]
)
open class RunModel(
    @PrimaryKey
    var crUid: String = "",
    var crAttemptId: String? = null,
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
    var premium: Boolean = false,
    var whatsappLink: String? = "",
    var crRunEnd: String = "",
    var totalContents: Int = 0,
    var completedContents: Int = 0,
    var mentorApproved: Boolean = false,
    var completionThreshold: Int = 90,
    var productId: Int = 0,
    var recommended: Boolean = false,
    var hits: Int = 0
)
