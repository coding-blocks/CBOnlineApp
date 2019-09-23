package com.codingblocks.cbonlineapp.database.models

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

class SectionContentHolder {
    @Entity(
        primaryKeys = ["section_id", "content_id"],
        indices = [
            Index(value = ["section_id"]),
            Index(value = ["content_id"])
        ],
        foreignKeys = [
            ForeignKey(
                entity = SectionModel::class,
                parentColumns = ["csid"],
                childColumns = ["section_id"]
            ),
            ForeignKey(
                entity = ContentModel::class,
                parentColumns = ["ccid"],
                childColumns = ["content_id"]
            )
        ]
    )
    data class SectionWithContent(
        @ColumnInfo(name = "section_id") val sectionId: String,
        @ColumnInfo(name = "content_id") val contentId: String,
        val order: Int
    )

    class SectionContentPair(
        @Embedded
        var section: SectionModel,
        @Embedded
        var content: ContentModel
    )

    data class SectionAndItsContents(
        val section: SectionModel,
        val contents: List<ContentModel>
    )

    companion object {
        fun groupContentBySection(sectionAndContent: List<SectionContentPair>): List<SectionAndItsContents> {
            return mutableListOf<SectionAndItsContents>().also { items ->
                sectionAndContent
                    .groupBy(keySelector = { it.section }, valueTransform = { it.content })
                    .forEach { items.add(SectionAndItsContents(it.key, it.value)) }
            }
        }
    }
}
