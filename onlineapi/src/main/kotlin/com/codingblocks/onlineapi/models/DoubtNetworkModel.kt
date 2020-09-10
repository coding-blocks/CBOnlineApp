package com.codingblocks.onlineapi.models

import com.github.jasminb.jsonapi.annotations.Relationship
import com.github.jasminb.jsonapi.annotations.Type

@Type("doubts", "doubt")
data class DoubtNetworkModel(
    val body: String = "",
    val title: String = "",
    var status: String = "PENDING",
    val discourseTopicId: String = "",
    val conversationId: String? = null,
    @Relationship("run_attempt", "run-attempt")
    val runAttempt: RunAttemptNetworkModel? = null,
    @Relationship("content")
    val content: LectureContent? = null,
    val createdAt: String = "",
    val categoryId: Int? = 0,
    val resolvedById: String? = null,
    val acknowledgedAt: String? = null,
    val resolvedAt: String? = null,
    val firebaseRef: String? = null,
    @Relationship("resolved_by", "resolved-by")
    val resolvedBy: User? = null
) : BaseNetworkModel() {
    constructor(
        id: String,
        title: String,
        body: String,
        discourseTopicId: String,
        runAttempt: RunAttemptNetworkModel?,
        conversationId: String?,
        content: LectureContent?,
        status: String,
        createdAt: String
    ) : this(title, body, status, discourseTopicId, conversationId, runAttempt, content, createdAt) {
        super.id = id
    }

    constructor(
        id: String?,
        title: String,
        body: String,
        runAttempt: RunAttemptNetworkModel?,
        content: LectureContent?
    ) : this(title = title, body = body, runAttempt = runAttempt, content = content)

    constructor(id: String) : this() {
        super.id = id
    }
}
