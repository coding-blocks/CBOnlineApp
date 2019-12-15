package com.codingblocks.cbonlineapp.course

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
import com.codingblocks.cbonlineapp.util.resultLiveData
import com.codingblocks.onlineapi.Clients
import com.codingblocks.onlineapi.getResult
import com.codingblocks.onlineapi.models.Course
import com.codingblocks.onlineapi.models.CourseFeatures
import com.codingblocks.onlineapi.models.Instructor
import com.codingblocks.onlineapi.models.Runs
import org.jetbrains.anko.AnkoLogger

class CourseRepository(
    private val courseWithInstructorDao: CourseWithInstructorDao,
    private val courseDao: CourseDao,
    private val runDao: CourseRunDao,
    private val instructorDao: InstructorDao,
    private val featuresDao: FeaturesDao) : AnkoLogger {

    suspend fun getRating(id: String) = Clients.api.getCourseRating(id)

    suspend fun getCourseSections(id: String) = Clients.onlineV2JsonApi.getSections(id)

    fun getCourse(id: String) =
        resultLiveData(
            databaseQuery = { courseWithInstructorDao.getCourse(id) },
            networkCall = { getResult { Clients.onlineV2JsonApi.getCourse(id) } },
            saveCallResult = { insertCourse(it) }
        )

    private suspend fun insertCourse(course: Course) {
        with(course) {
            courseDao.insert(
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
            course.instructors?.let { insertInstructors(it, course.id) }
            coursefeatures?.let { insertCourseFeatures(it, id) }
            course.runs?.let { insertCourseRuns(it, course.id) }
        }
    }

    private suspend fun insertInstructors(instructors: ArrayList<Instructor>, id: String) {
        instructors.forEach { instructor ->
            instructorDao.insert(
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

    private suspend fun insertCourseFeatures(courseFeatures: ArrayList<CourseFeatures>, id: String) {
        courseFeatures.forEach {
            featuresDao.insert(CourseFeatureModel(icon = it.icon, text = it.text, crCourseId = id))
        }
    }

    private suspend fun insertCourseRuns(runs: ArrayList<Runs>, id: String) {
        runs.forEach {
            runDao.insertNew(
                RunModel(
                    it.id,
                    null,
                    it.name,
                    it.description,
                    it.enrollmentStart,
                    it.enrollmentEnd,
                    it.start,
                    it.end,
                    it.price,
                    it.mrp ?: "",
                    id
                )
            )
        }
    }

}
