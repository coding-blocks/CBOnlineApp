package com.codingblocks.cbonlineapp.database.models

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.Junction
import androidx.room.Relation

class SectionContentHolder {
    @Entity(
        primaryKeys = ["section_id", "content_id"],
        indices = [
            Index(value = ["section_id"]),
            Index(value = ["content_id"])
        ]
    )
    data class SectionWithContent(
        @ColumnInfo(name = "section_id") val sectionId: String,
        @ColumnInfo(name = "content_id") val contentId: String,
        val order: Int
    )

    data class SectionContentPair(
        @Embedded
        var section: SectionModel,
        @Relation(
            parentColumn = "csid",
            entity = ContentModel::class,
            entityColumn = "ccid",
            associateBy = Junction(
                value = SectionWithContent::class,
                parentColumn = "section_id",
                entityColumn = "content_id"
            )
        ) var contents: List<ContentModel>
    )

    data class NextContent(
        var sectionId: String,
        var contentId: String,
        var contentable: String
    )

    data class DownloadableContent(
        var videoId: String,
        var sectionId: String,
        var contentId: String
    ) {
        @Ignore
        var isDownloaded: Boolean = false
    }
}
