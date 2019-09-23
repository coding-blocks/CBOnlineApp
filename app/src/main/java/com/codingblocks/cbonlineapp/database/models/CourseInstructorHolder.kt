package com.codingblocks.cbonlineapp.database.models

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

class CourseInstructorHolder {
    @Entity(
        primaryKeys = ["course_id", "instructor_id"],
        indices = [
            Index(value = ["course_id"]),
            Index(value = ["instructor_id"])
        ],
        foreignKeys = [
            ForeignKey(
                entity = CourseModel::class,
                parentColumns = ["cid"],
                childColumns = ["course_id"]
            ),
            ForeignKey(
                entity = InstructorModel::class,
                parentColumns = ["uid"],
                childColumns = ["instructor_id"]
            )
        ]
    )
    class CourseWithInstructor(
        @ColumnInfo(name = "course_id") val courseId: String,
        @ColumnInfo(name = "instructor_id") val instructorId: String
    )

    class CourseRunPair : RunModel() {
        @Embedded
        var course: CourseModel = CourseModel()
    }

    class CourseInstructorPair(
        @Embedded
        var courseRun: CourseRunPair,
        @Embedded
        var instructor: InstructorModel
    )

    data class CourseAndItsInstructor(
        val courseRun: CourseRunPair,
        val instructors: List<InstructorModel>
    )

    companion object {
        fun groupInstructorByRun(courseAndInstructor: List<CourseInstructorPair>): List<CourseAndItsInstructor> {
            val list = mutableListOf<String>()
            return mutableListOf<CourseAndItsInstructor>().also { items ->
                courseAndInstructor
                    .groupBy(keySelector = { it.courseRun.crUid }, valueTransform = { it.instructor })
                    .forEach {
                        courseAndInstructor.forEach { run ->
                            if (run.courseRun.crUid == it.key && !list.contains(it.key)) {
                                list.add(it.key)
                                items.add(CourseAndItsInstructor(run.courseRun, it.value))
                                return@forEach
                            }
                        }
                    }
            }
        }
    }
}
