package com.codingblocks.cbonlineapp.database.models

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import androidx.room.Junction
import androidx.room.Relation

@Entity(primaryKeys = ["course_id", "instructor_id"],
    indices = [
        Index(value = ["course_id"]),
        Index(value = ["instructor_id"])
    ])
class CourseWithInstructor(
    @ColumnInfo(name = "course_id") val courseId: String,
    @ColumnInfo(name = "instructor_id") val instructorId: String
)

class CourseRunPair : RunWithAttempt() {
    @Embedded
    var course: CourseModel = CourseModel()
}

data class CourseInstructorPair(
    @Embedded
    var courseRun: CourseRunPair,
    @Relation(
        parentColumn = "cid",
        entity = InstructorModel::class,
        entityColumn = "uid",
        associateBy = Junction(
            value = CourseWithInstructor::class,
            parentColumn = "course_id",
            entityColumn = "instructor_id"
        )
    )
    var instructor: List<InstructorModel>
)
