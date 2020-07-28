package com.codingblocks.cbonlineapp.database.models

import androidx.annotation.Nullable
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.codingblocks.cbonlineapp.database.ListObject

@Entity
data class ContentModel(
    @PrimaryKey
    var ccid: String,
    var progress: String,
    var progressId: String,
    var title: String,
    var contentDuration: Long,
    var contentable: String,
    var order: Int,
    var attempt_id: String,
    var sectionTitle: String = "",
    @Embedded
    @Nullable
    var contentLecture: ContentLecture = ContentLecture(),
    @Embedded
    @Nullable
    var contentDocument: ContentDocument = ContentDocument(),
    @Embedded
    @Nullable
    var contentVideo: ContentVideo = ContentVideo(),
    @Embedded
    @Nullable
    var contentQna: ContentQnaModel = ContentQnaModel(),
    @Embedded
    @Nullable
    var contentCode: ContentCodeChallenge = ContentCodeChallenge(),
    @Embedded
    @Nullable
    var contentCsv: ContentCsvModel = ContentCsvModel()
) : ListObject() {
    @Ignore
    override fun getType(): Int = TYPE_CONTENT

    @Ignore
    var sectionId: String = ""

    @Ignore
    var premium: Boolean = true
}
