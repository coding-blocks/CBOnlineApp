package com.codingblocks.cbonlineapp.database.models

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Junction
import androidx.room.Relation

class CourseInstructorHolder {
    @Entity(primaryKeys = ["course_id", "instructor_id"])
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

    data class CourseAndItsInstructor(
        val courseRun: CourseRunPair,
        val instructors: List<InstructorModel>
    )

    companion object {
        fun groupInstructorByRun(courseAndInstructor: List<CourseInstructorPair>): List<CourseAndItsInstructor> {
            val list = mutableListOf<String>()
            return mutableListOf<CourseAndItsInstructor>().also { items ->
                courseAndInstructor
//                    .groupBy(keySelector = { it.courseRun.crUid }, valueTransform = { it.instructor })
//                    .forEach {
//                        courseAndInstructor.forEach { run ->
//                            if (run.courseRun.crUid == it.key && !list.contains(it.key)) {
//                                list.add(it.key)
//                                items.add(CourseAndItsInstructor(run.courseRun, it.value))
//                                return@forEach
//                            }
//                        }
//                    }
            }
        }
    }
}
