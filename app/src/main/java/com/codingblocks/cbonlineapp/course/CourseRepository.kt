package com.codingblocks.cbonlineapp.course

import com.codingblocks.onlineapi.Clients
import com.codingblocks.onlineapi.safeApiCall
import org.jetbrains.anko.AnkoLogger

class CourseRepository() : AnkoLogger {

    suspend fun getRating(id: String) = Clients.api.getCourseRating(id)

    suspend fun getCourse(id: String) = safeApiCall { Clients.onlineV2JsonApi.getCourse(id) }

    suspend fun getSuggestedCourses(offset: Int = 0, page: Int = 12) = safeApiCall { Clients.onlineV2JsonApi.getRecommendedCourses(offset = offset, page = page) }

    suspend fun getProjects(id: String) = Clients.onlineV2JsonApi.getProject(id)

    suspend fun getSection(id: String) = Clients.onlineV2JsonApi.getSections(id)
}
