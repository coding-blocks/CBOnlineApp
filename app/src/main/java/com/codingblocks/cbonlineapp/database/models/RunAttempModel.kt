package com.codingblocks.cbonlineapp.database.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

/**
 * @author aggarwalpulkit596
 */
@Entity
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
    val lastAccessedAt: String,
    val runId: String,
    val certificateUrl: String = "",
    val runTier: String = ""
) {
    constructor() : this("", end = "", doubtSupport = " ", completedContents = 0, lastAccessedAt = "", runId = "")
}

open class RunWithAttempt(
    @Embedded
    public var run: RunModel,
    @Relation(
        parentColumn = "crUid",
        entityColumn = "runId"
    )
    public var runAttempt: RunAttemptModel
) {
    constructor() : this(RunModel(), RunAttemptModel())
}
