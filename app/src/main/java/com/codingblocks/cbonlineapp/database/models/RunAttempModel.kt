package com.codingblocks.cbonlineapp.database.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.codingblocks.onlineapi.models.BaseModel

/**
 * @author aggarwalpulkit596
 */
@Entity(
    indices = [Index("crCourseId")],
    foreignKeys = [
        ForeignKey(
            entity = CourseModel::class,
            parentColumns = ["cid"],
            childColumns = ["attemptId"],
            onDelete = ForeignKey.CASCADE)
    ]
)
data class RunAttemptModel(
    @PrimaryKey
    val attemptId: String,
    val certificateApproved: Boolean = false,
    val end: String,
    val premium: Boolean = false,
    val revoked: Boolean = false,
    val approvalRequested: Boolean = false,
    val doubtSupport: String,
    val completedContents: Int,
    val lastAccessedAt: String
) : BaseModel()
