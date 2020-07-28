package com.codingblocks.cbonlineapp.course

import com.codingblocks.onlineapi.CBOnlineLib
import com.codingblocks.onlineapi.models.Wishlist
import com.codingblocks.onlineapi.safeApiCall
import org.jetbrains.anko.AnkoLogger

class CourseRepository() : AnkoLogger {

    suspend fun getRating(id: String) = CBOnlineLib.api.getCourseRating(id)

    suspend fun getCourse(id: String) = safeApiCall { CBOnlineLib.onlineV2JsonApi.getCourse(id) }

    suspend fun getSuggestedCourses(offset: Int = 0, page: Int = 12) = safeApiCall { CBOnlineLib.onlineV2JsonApi.getRecommendedCourses(offset = offset, page = page) }

    suspend fun getAllCourses(offset: String) = safeApiCall { CBOnlineLib.onlineV2JsonApi.getAllCourses(offset = offset) }

    suspend fun getProjects(id: String) = safeApiCall { CBOnlineLib.onlineV2JsonApi.getProject(id) }

    suspend fun getSection(id: String) = safeApiCall { CBOnlineLib.onlineV2JsonApi.getSections(id) }

    suspend fun clearCart() = safeApiCall { CBOnlineLib.api.clearCart() }

    suspend fun addToCart(id: String) = safeApiCall { CBOnlineLib.api.addToCart(id) }

    suspend fun enrollToTrial(id: String) = safeApiCall { CBOnlineLib.api.enrollTrial(id) }

    suspend fun getTracks() = safeApiCall { CBOnlineLib.onlineV2JsonApi.getTracks() }

    suspend fun findCourses(query: String) = safeApiCall { CBOnlineLib.onlineV2JsonApi.findCourses(query = "%$query%") }

    suspend fun checkIfWishlisted(id: String) = safeApiCall { CBOnlineLib.onlineV2JsonApi.checkIfWishlisted(id) }

    suspend fun removeWishlist(id: String) = safeApiCall { CBOnlineLib.onlineV2JsonApi.removeWishlist(id) }

    suspend fun addWishlist(wishList: Wishlist) = safeApiCall { CBOnlineLib.onlineV2JsonApi.addWishlist(wishList) }
}
