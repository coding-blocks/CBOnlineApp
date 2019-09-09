package com.codingblocks.cbonlineapp.database.models

import androidx.room.*

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

    class CourseRunPair(
        @Embedded
        var run: RunModel,
        @Embedded
        var course: CourseModel
    )

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
        fun groupInstructor(courseAndInstructor: List<CourseInstructorPair>): List<CourseAndItsInstructor> {
            return mutableListOf<CourseAndItsInstructor>().also { items ->
                val list = mutableListOf<String>()
                courseAndInstructor
                    .groupBy(keySelector = { it.courseRun.run }, valueTransform = { it.instructor })
                    .forEach {
                        courseAndInstructor.forEach { run ->
                            it.key
                            if (run.courseRun.run.crAttemptId == it.key.crAttemptId && !list.contains(it.key.crAttemptId)) {
                                list.add(it.key.crAttemptId)
                                items.add(CourseAndItsInstructor(CourseRunPair(it.key, run.courseRun.course), it.value))
                                return@forEach
                            }


                        }
                    }
            }
        }
    }


}
