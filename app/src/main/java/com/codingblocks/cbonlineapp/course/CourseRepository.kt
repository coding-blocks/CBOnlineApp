package com.codingblocks.cbonlineapp.course

import com.codingblocks.cbonlineapp.database.CourseDao
import com.codingblocks.cbonlineapp.database.CourseRunDao
import com.codingblocks.cbonlineapp.database.CourseWithInstructorDao
import com.codingblocks.cbonlineapp.database.FeaturesDao
import com.codingblocks.cbonlineapp.database.InstructorDao
import com.codingblocks.onlineapi.Clients
import com.codingblocks.onlineapi.safeApiCall
import org.jetbrains.anko.AnkoLogger

class CourseRepository(
    private val courseWithInstructorDao: CourseWithInstructorDao,
    private val courseDao: CourseDao,
    private val runDao: CourseRunDao,
    private val instructorDao: InstructorDao,
    private val featuresDao: FeaturesDao
) : AnkoLogger {

    suspend fun getRating(id: String) = Clients.api.getCourseRating(id)

    suspend fun getCourse(id: String) = safeApiCall { Clients.onlineV2JsonApi.getCourse(id) }

    suspend fun getSuggestedCourses() = safeApiCall { Clients.onlineV2JsonApi.getRecommendedCourses() }

    suspend fun getProjects(id: String) = Clients.onlineV2JsonApi.getProject(id)

    suspend fun getSection(id: String) = Clients.onlineV2JsonApi.getSections(id)
}
