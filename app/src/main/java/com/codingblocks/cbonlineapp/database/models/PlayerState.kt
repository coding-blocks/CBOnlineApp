package com.codingblocks.cbonlineapp.database.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [(ForeignKey(
        entity = SectionModel::class,
        parentColumns = ["csid"],
        childColumns = ["sectionId"],
        onDelete = ForeignKey.CASCADE
    )), (ForeignKey(
        entity = ContentModel::class,
        parentColumns = ["ccid"],
        childColumns = ["contentId"],
        onDelete = ForeignKey.CASCADE
    ))], indices = [Index(value = ["attemptId"], unique = true)])
class PlayerState(
    var attemptId: String,
    var sectionId: String,
    val contentId: String,
    var position: Long,
    val lastAccessedAt: Long = System.currentTimeMillis(),
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @Ignore
    var sectionName: String = "",
    @Ignore
    var contentTitle: String = "",
    @Ignore
    var contentDuration: Long = 0L,
    var thumbnail: String = ""
)
