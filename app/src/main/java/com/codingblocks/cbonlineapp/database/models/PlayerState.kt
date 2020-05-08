package com.codingblocks.cbonlineapp.database.models

import androidx.room.Entity
import androidx.room.ForeignKey
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
    var contentId: String,
    var position: Long,
    var lastAccessedAt: Long = System.currentTimeMillis(),
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var sectionName: String = "",
    var contentTitle: String = "",
    var contentDuration: Long = 0L,
    var thumbnail: String = ""
) {
    constructor() : this("", "", "", 0L)

    fun getProgress(): Int {
        return ((position.toFloat() / contentDuration) * 100).toInt()
    }
}
