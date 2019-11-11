package com.codingblocks.cbonlineapp.home

import com.codingblocks.cbonlineapp.database.CourseDao
import com.codingblocks.cbonlineapp.database.CourseRunDao
import com.codingblocks.cbonlineapp.database.CourseWithInstructorDao
import com.codingblocks.cbonlineapp.database.FeaturesDao
import com.codingblocks.cbonlineapp.database.InstructorDao
import com.codingblocks.cbonlineapp.database.models.CourseFeatureModel
import com.codingblocks.cbonlineapp.database.models.CourseModel
import com.codingblocks.cbonlineapp.database.models.CourseWithInstructor
import com.codingblocks.cbonlineapp.database.models.InstructorModel
import com.codingblocks.cbonlineapp.database.models.RunModel
import com.codingblocks.cbonlineapp.util.extensions.greater
import com.codingblocks.onlineapi.models.Course
import com.codingblocks.onlineapi.models.CourseFeatures
import com.codingblocks.onlineapi.models.Instructor
import com.codingblocks.onlineapi.models.Runs

class HomeRepository(
    private val courseWithInstructorDao: CourseWithInstructorDao,
    private val courseDao: CourseDao,
    private val runDao: CourseRunDao,
    private val instructorDao: InstructorDao,
    private val featuresDao: FeaturesDao
) {

    fun getAllCourses() = courseWithInstructorDao.getCourses()

    fun getRecommendedCourses() = courseWithInstructorDao.getRecommendedCourses()

    suspend fun insertCourse(course: Course, recommended: Boolean = true) {
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
                    categoryId,
                    faq

                ))
            course.instructors?.let { insertInstructor(it, course.id) }
            coursefeatures?.let { insertCourseFeatures(it, id) }
            var list = course.runs?.filter { run ->
                !run.enrollmentStart.greater() && run.enrollmentEnd.greater() && !run.unlisted
            }?.sortedWith(compareBy { run -> run.price })
            if (list != null) {
                if (list.isEmpty()) {
                    list =
                        course.runs?.sortedWith(compareBy { run -> run.price })
                }
            }
            list?.get(0)?.let { insertRun(it, course.id, recommended) }
        }
    }

    private suspend fun insertCourseFeatures(courseFeatures: List<CourseFeatures>, id: String) {
        courseFeatures.forEach {
            featuresDao.insert(CourseFeatureModel(icon = it.icon, text = it.text, crCourseId = id))
        }
    }

    suspend fun insertRun(run: Runs, id: String, recommended: Boolean) {
        with(run) {
            runDao.insertNew(
                RunModel(
                    id,
                    null,
                    name,
                    description,
                    enrollmentStart,
                    enrollmentEnd,
                    start,
                    end,
                    price,
                    mrp ?: "",
                    id,
                    crUpdatedAt = updatedAt,
                    recommended = recommended
                )
            )
        }
    }

    suspend fun insertInstructor(instructors: List<Instructor>, id: String) {
        instructors.forEach { instructor ->
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
