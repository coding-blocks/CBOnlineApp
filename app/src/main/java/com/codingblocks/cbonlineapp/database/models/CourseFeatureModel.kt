package com.codingblocks.cbonlineapp.database.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    indices = [Index("crCourseId")],
    foreignKeys = [ForeignKey(entity = CourseModel::class,
        parentColumns = ["cid"],
        childColumns = ["crCourseId"])])
data class CourseFeatureModel(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val icon: String,
    val text: String,
    var crCourseId: String = ""
)
