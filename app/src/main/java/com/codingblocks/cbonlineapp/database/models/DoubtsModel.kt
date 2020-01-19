package com.codingblocks.cbonlineapp.database.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DoubtsModel(
    @PrimaryKey
    var dbtUid: String,
    var title: String,
    var body: String,
    var contentId: String,
    var status: String,
    var runAttemptId: String,
    var discourseTopicId: String,
    var conversationId: String?,
    var createdAt: String
) {
    constructor() : this("", "", "", "", "",
        "", "", null, "")
}
