package com.codingblocks.cbonlineapp.course

import com.codingblocks.cbonlineapp.database.WishlistDao
import com.codingblocks.cbonlineapp.database.models.CourseModel
import com.codingblocks.cbonlineapp.database.models.WishlistModel
import com.codingblocks.onlineapi.Clients
import com.codingblocks.onlineapi.models.Course
import com.codingblocks.onlineapi.models.Wishlist
import com.codingblocks.onlineapi.safeApiCall
import org.jetbrains.anko.AnkoLogger

class CourseRepository(
    val wishlistDao: WishlistDao
) : AnkoLogger {

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

    suspend fun checkIfWishlisted(id: String) = safeApiCall { Clients.onlineV2JsonApi.checkIfWishlisted(id) }

    suspend fun removeFromWishlist(id: String) = safeApiCall { Clients.onlineV2JsonApi.removeFromWishlist(id) }

    suspend fun addToWishlist(wishList: Wishlist) = safeApiCall { Clients.onlineV2JsonApi.addToWishlist(wishList) }

    suspend fun addWishlist(wishList: Wishlist) {
        val model = WishlistModel(
            wishList.id,
            with(wishList.course!!){
                CourseModel(
                    id,
                    title,
                    subtitle,
                    logo,
                    summary,
                    promoVideo?:"",
                    difficulty,
                    reviewCount,
                    rating,
                    slug,
                    coverImage?:"",
                    categoryId?:-1
                )
            }
            , wishList.user
        )
        if (wishlistDao.getWishlistsByCourse(wishList.course!!.id)!=null){
            wishlistDao.update(model)
        }else{
            wishlistDao.insertNew(model)
        }
    }

    suspend fun removeFromWishlist(course: Course){
        wishlistDao.deleteCourseID(course.id)
    }
}
