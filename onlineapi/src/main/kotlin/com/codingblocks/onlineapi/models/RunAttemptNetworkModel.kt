package com.codingblocks.onlineapi.models

import com.github.jasminb.jsonapi.annotations.Relationship
import com.github.jasminb.jsonapi.annotations.Type

@Type("run-attempts", "run_attempts")
data class RunAttemptNetworkModel(
    val certificateApproved: Boolean = false,
    val end: String = "",
    val premium: Boolean = false,
    val revoked: Boolean = false,
    val approvalRequested: Boolean = false,
    val doubtSupport: String? = "",
    val completedContents: Int = 0,
    val lastAccessedAt: String? = "",
    val runTier: String? = null,
    val paused: Boolean = false,
    val pauseTimeLeft: String? = null,
    val lastPausedLeft: String? = null,
    @Relationship("run")
    val run: Runs? = null,
    @Relationship("certificate")
    val certifcate: Certificate? = null,
) : BaseNetworkModel() {
    constructor(id: String) : this() {
        super.id = id
    }
}
