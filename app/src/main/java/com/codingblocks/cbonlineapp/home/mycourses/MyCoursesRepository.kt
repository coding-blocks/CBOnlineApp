package com.codingblocks.cbonlineapp.home.mycourses

import com.codingblocks.cbonlineapp.database.CourseDao
import com.codingblocks.cbonlineapp.database.CourseRunDao
import com.codingblocks.cbonlineapp.database.CourseWithInstructorDao
import com.codingblocks.cbonlineapp.database.InstructorDao
import com.codingblocks.cbonlineapp.database.models.CourseModel
import com.codingblocks.cbonlineapp.database.models.CourseWithInstructor
import com.codingblocks.cbonlineapp.database.models.InstructorModel
import com.codingblocks.cbonlineapp.database.models.RunModel
import com.codingblocks.onlineapi.models.Instructor
import com.codingblocks.onlineapi.models.MyCourse
import com.codingblocks.onlineapi.models.MyCourseRuns

class MyCoursesRepository(
    private val courseWithInstructorDao: CourseWithInstructorDao,
    private val courseDao: CourseDao,
    private val runDao: CourseRunDao,
    private val instructorDao: InstructorDao
) {
    fun getMyRuns() = courseWithInstructorDao.getMyRuns()

    fun getTopRun() = courseWithInstructorDao.getTopRun()

    suspend fun insertCourse(course: MyCourse) {
        with(course) {
            courseDao.insertNew(
                CourseModel(
                    id,
                    title,
                    subtitle,
                    logo,
                    summary,
                    promoVideo,
                    difficulty,
                    reviewCount,
                    rating,
                    slug,
                    coverImage,
                    categoryId
                ))
        }
    }

    suspend fun insertRun(run: MyCourseRuns, progress: Double, totalContents: Int, completedContents: Int, refresh: Boolean) {
        with(run) {
            val newRun = RunModel(
                id,
                runAttempts?.get(0)?.id ?: "",
                name,
                description,
                enrollmentStart,
                enrollmentEnd,
                start,
                end,
                price,
                mrp ?: "",
                course?.id ?: "",
                updatedAt,
                progress,
                runAttempts?.get(0)?.premium
                    ?: false,
                whatsappLink,
                runAttempts?.get(0)?.end
                    ?: "",
                totalContents,
                completedContents,
                runAttempts?.get(0)?.certificateApproved
                    ?: false,
                completionThreshold,
                productId
            )
            val oldRun = runDao.getRunById(
                runAttempts?.get(0)?.id ?: ""
            )
            if (oldRun == null) {
                runDao.insertNew(newRun)
            } else if (oldRun.progress != progress || refresh) {
                newRun.hits = oldRun.hits
                runDao.update(newRun)
            }
        }
    }

    suspend fun insertInstructor(instructor: Instructor, id: String) {
        with(instructor) {
            instructorDao.insertNew(
                InstructorModel(
                    instructor.id,
                    instructor.name,
                    instructor.description ?: "",
                    instructor.photo,
                    instructor.email,
                    instructor.sub
                )
            )
            courseWithInstructorDao.insert(
                CourseWithInstructor(courseId = id, instructorId = instructor.id)
            )
        }
    }
}
