package com.codingblocks.cbonlineapp.dashboard.mycourses

import androidx.lifecycle.LiveData
import com.codingblocks.cbonlineapp.database.CourseDao
import com.codingblocks.cbonlineapp.database.CourseRunDao
import com.codingblocks.cbonlineapp.database.CourseWithInstructorDao
import com.codingblocks.cbonlineapp.database.InstructorDao
import com.codingblocks.cbonlineapp.database.models.CourseInstructorPair
import com.codingblocks.cbonlineapp.database.models.CourseModel
import com.codingblocks.cbonlineapp.database.models.CourseWithInstructor
import com.codingblocks.cbonlineapp.database.models.InstructorModel
import com.codingblocks.cbonlineapp.database.models.RunModel
import com.codingblocks.onlineapi.Clients
import com.codingblocks.onlineapi.ResultWrapper
import com.codingblocks.onlineapi.models.Course
import com.codingblocks.onlineapi.models.Instructor
import com.codingblocks.onlineapi.models.Runs
import com.codingblocks.onlineapi.safeApiCall
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DashboardMyCoursesRepository(
    private val courseWithInstructorDao: CourseWithInstructorDao,
    private val courseDao: CourseDao,
    private val runDao: CourseRunDao,
    private val instructorDao: InstructorDao
) {

    suspend fun fetchMyCourses(offset: String) = safeApiCall {
        Clients.onlineV2JsonApi.getMyCourses(offset = offset)
    }

    suspend fun insertCourses(runs: List<Runs>) {
        runs.forEach { run ->
            with(run) {
                val response = withContext(Dispatchers.IO) { insertCourse(course) }
                if (!runAttempts.isNullOrEmpty()) {
                    val model = RunModel(
                        id,
                        name ?: "",
                        description ?: "",
                        enrollmentStart ?: "",
                        enrollmentEnd ?: "",
                        start ?: "",
                        end ?: "",
                        price ?: "",
                        mrp ?: price ?: "",
                        course?.id ?: "",
                        updatedAt,
                        whatsappLink,
                        runAttempts?.first()?.id ?: "",
                        runAttempts?.first()?.premium ?: false,
                        runAttempts?.first()?.end ?: "",
                        runAttempts?.first()?.approvalRequested ?: false,
                        runAttempts?.first()?.certificateApproved ?: false,
                        totalContents,
                        completedContents,
                        (totalContents / completedContents).toDouble(),
                        completionThreshold ?: 0,
                        goodiesThreshold ?: 0,
                        productId ?: 0
                    )
                    if (response == -2L && !runDao.getRun(id).isNullOrEmpty()) {
                        runDao.update(model)
                    } else if (response != -1L)
                        runDao.insert(model)
                }
            }
        }
    }

    private suspend fun insertCourse(course: Course?): Long {
        val refresh = courseDao.getCourseById(course?.id ?: "").isNullOrEmpty()
        if (course != null && refresh)
            return with(course) {
                instructors?.let { getInstructors(it, id) }
                courseDao.insert(CourseModel(
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
            } else if (!refresh)
            return -2L
        else
            return -1L
    }

    private suspend fun getInstructors(instructors: ArrayList<Instructor>, courseId: String) {
        instructors.forEach {
            if (instructorDao.getInstructorById(it.id).isNullOrEmpty())
                when (val response = safeApiCall { Clients.onlineV2JsonApi.instructorsById(it.id) }) {
                    is ResultWrapper.Success -> {
                        if (response.value.isSuccessful)
                            response.value.body()?.let {
                                instructorDao.insert(InstructorModel(it.id, it.name, it.description
                                    ?: "", it.photo, it.email, it.sub))
                                courseWithInstructorDao.insert(
                                    CourseWithInstructor(courseId = courseId, instructorId = it.id)
                                )
                            }
                    }
                }
            courseWithInstructorDao.insert(
                CourseWithInstructor(courseId = courseId, instructorId = it.id)
            )
        }
    }

    fun getMyRuns(query: String = "Recently Accessed"): LiveData<List<CourseInstructorPair>> {
        return when (query) {
            "Recently Accessed" -> courseWithInstructorDao.getRecentRuns()
            "Expired Courses" -> courseWithInstructorDao.getExpiredRuns(System.currentTimeMillis() / 1000)
            else -> courseWithInstructorDao.getMyRuns()
        }
    }
}
