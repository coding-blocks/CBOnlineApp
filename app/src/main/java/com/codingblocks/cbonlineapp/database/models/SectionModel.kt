package com.codingblocks.cbonlineapp.database.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    indices = [Index("run_id")],
    foreignKeys = [(ForeignKey(
        entity = RunModel::class,
        parentColumns = ["crUid"],
        childColumns = ["run_id"],
        onDelete = ForeignKey.CASCADE // or CASCADE
    ))]
)
data class SectionModel(
    @PrimaryKey
    var csid: String,
    var name: String,
    var sectionOrder: Int,
    var premium: Boolean,
    var status: String,
    var run_id: String,
    var attemptId: String
)
