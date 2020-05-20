package com.codingblocks.cbonlineapp.course

import com.codingblocks.onlineapi.Clients
import com.codingblocks.onlineapi.safeApiCall
import org.jetbrains.anko.AnkoLogger

class CourseRepository() : AnkoLogger {

    suspend fun getRating(id: String) = Clients.api.getCourseRating(id)

    suspend fun getCourse(id: String) = safeApiCall { Clients.onlineV2JsonApi.getCourse(id) }

    suspend fun getSuggestedCourses(offset: Int = 0, page: Int = 12) = safeApiCall { Clients.onlineV2JsonApi.getRecommendedCourses(offset = offset, page = page) }

    suspend fun getAllCourses(offset: String) = safeApiCall { Clients.onlineV2JsonApi.getAllCourses(offset = offset) }

    suspend fun getProjects(id: String) = safeApiCall { Clients.onlineV2JsonApi.getProject(id) }

    suspend fun getSection(id: String) = safeApiCall { Clients.onlineV2JsonApi.getSections(id) }

    suspend fun clearCart() = safeApiCall { Clients.api.clearCart() }

    suspend fun addToCart(id: String) = safeApiCall { Clients.api.addToCart(id) }

    suspend fun enrollToTrial(id: String) = safeApiCall { Clients.api.enrollTrial(id) }

    suspend fun getTracks() = safeApiCall { Clients.onlineV2JsonApi.getTracks() }

    suspend fun findCourses(query: String) = safeApiCall { Clients.onlineV2JsonApi.findCourses(query = "%$query%") }
}
