package com.codingblocks.cbonlineapp.dashboard.home

import com.codingblocks.cbonlineapp.database.CourseWithInstructorDao
import com.codingblocks.cbonlineapp.database.PlayerDao
import com.codingblocks.cbonlineapp.database.RunPerformanceDao
import com.codingblocks.cbonlineapp.database.WishlistDao
import com.codingblocks.cbonlineapp.database.models.CourseModel
import com.codingblocks.cbonlineapp.database.models.RunPerformance
import com.codingblocks.cbonlineapp.database.models.WishlistModel
import com.codingblocks.cbonlineapp.util.PreferenceHelper
import com.codingblocks.cbonlineapp.util.extensions.getDistinct
import com.codingblocks.onlineapi.Clients
import com.codingblocks.onlineapi.models.PerformanceResponse
import com.codingblocks.onlineapi.models.Player
import com.codingblocks.onlineapi.models.User
import com.codingblocks.onlineapi.models.Wishlist
import com.codingblocks.onlineapi.models.Course
import com.codingblocks.onlineapi.safeApiCall

class DashboardHomeRepository(
    private val courseWithInstructorDao: CourseWithInstructorDao,
    private val runPerformanceDao: RunPerformanceDao,
    val prefs: PreferenceHelper,
    val playerDao: PlayerDao,
    val wishlistDao: WishlistDao
) {
    fun insertUser(user: User) {
        with(user) {
            prefs.apply {
                SP_ONEAUTH_ID = oneauthId ?: ""
                SP_USER_ID = id
                SP_USER_IMAGE = photo ?: "empty"
                SP_USER_NAME = "$firstname $lastname"
                SP_NAME = "$username"
                SP_ROLE_ID = roleId?:0
                SP_ADMIN = roleId == 1 || roleId == 3
            }
        }
    }

    suspend fun saveStats(body: PerformanceResponse, id: String) {
        runPerformanceDao.insert(
            RunPerformance(
                id,
                body.performance?.percentile ?: 0,
                body.performance?.remarks ?: "Average",
                body.averageProgress,
                body.userProgress
            )
        )
    }

    suspend fun saveWishlist(wishlist: List<Wishlist>?){
        wishlistDao.deleteAll()
        val models: ArrayList<WishlistModel> = ArrayList()
        wishlist!!.forEach {model->
            models.add(WishlistModel(
                model.id,
                with(model.course!!){
                    CourseModel(
                        id,
                        title,
                        subtitle,
                        logo,
                        summary,
                        promoVideo ?: "",
                        difficulty,
                        reviewCount,
                        rating,
                        slug,
                        coverImage ?: "",
                        categoryId ?: -1
                    )
                }

                , model.user
            ))
        }
        wishlistDao.insertAll(models)
    }

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

    suspend fun getWishlistsByCourse(id: String) = wishlistDao.getWishlistsByCourse(id)

    suspend fun removeFromWishlist(course: Course){
        wishlistDao.deleteCourseID(course.id)
    }

    fun getWishlistThree() = wishlistDao.getWishlistThree()
    fun getTopRun() = courseWithInstructorDao.getTopRun().getDistinct()
    fun getTopRunById(id: String) = courseWithInstructorDao.getRunById(id).getDistinct()
    fun getRunStats(it: String) = runPerformanceDao.getPerformance(it)
    fun getRecentlyPlayed() = playerDao.getPromotedStories()

    suspend fun updatePlayerId(player: Player) = safeApiCall { Clients.onlineV2JsonApi.setPlayerId(player) }
    suspend fun fetchLastAccessedRun() = safeApiCall { Clients.onlineV2JsonApi.getLastAccessed() }
    suspend fun getStats(id: String) = safeApiCall { Clients.api.getMyStats(id) }
    suspend fun fetchUser() = safeApiCall { Clients.onlineV2JsonApi.getMe() }
    suspend fun refreshToken() = safeApiCall { Clients.api.refreshToken(prefs.SP_JWT_REFRESH_TOKEN) }
    suspend fun fetchWishlist() = safeApiCall { Clients.onlineV2JsonApi.getWishlist() }
    suspend fun addToWishlist(wishList: Wishlist) = safeApiCall { Clients.onlineV2JsonApi.addToWishlist(wishList) }
    suspend fun removeFromWishlist(id: String) = safeApiCall { Clients.onlineV2JsonApi.removeFromWishlist(id) }
}
