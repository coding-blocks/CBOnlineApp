package com.codingblocks.cbonlineapp.dashboard.home

import com.codingblocks.cbonlineapp.database.CourseWithInstructorDao
import com.codingblocks.cbonlineapp.database.PlayerDao
import com.codingblocks.cbonlineapp.database.RunPerformanceDao
import com.codingblocks.cbonlineapp.database.models.RunPerformance
import com.codingblocks.cbonlineapp.util.PreferenceHelper
import com.codingblocks.cbonlineapp.util.livedata.getDistinct
import com.codingblocks.onlineapi.CBOnlineLib
import com.codingblocks.onlineapi.models.PerformanceResponse
import com.codingblocks.onlineapi.models.Player
import com.codingblocks.onlineapi.models.User
import com.codingblocks.onlineapi.models.Wishlist
import com.codingblocks.onlineapi.safeApiCall

class DashboardHomeRepository(
    private val courseWithInstructorDao: CourseWithInstructorDao,
    private val runPerformanceDao: RunPerformanceDao,
    val prefs: PreferenceHelper,
    val playerDao: PlayerDao
) {
    fun insertUser(user: User) {
        with(user) {
            prefs.apply {
                SP_ONEAUTH_ID = oneauthId ?: ""
                SP_USER_ID = id
                SP_USER_IMAGE = photo ?: "empty"
                SP_USER_NAME = "$firstname $lastname"
                SP_NAME = "$username"
                SP_ROLE_ID = roleId
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

    fun getTopRun() = courseWithInstructorDao.getTopRun().getDistinct()
    fun getTopRunById(id: String) = courseWithInstructorDao.getRunById(id).getDistinct()
    fun getRunStats(it: String) = runPerformanceDao.getPerformance(it)
    fun getRecentlyPlayed() = playerDao.getPromotedStories()

    suspend fun updatePlayerId(player: Player) = safeApiCall { CBOnlineLib.onlineV2JsonApi.setPlayerId(player) }
    suspend fun fetchLastAccessedRun() = safeApiCall { CBOnlineLib.onlineV2JsonApi.getLastAccessed() }
    suspend fun getStats(id: String) = safeApiCall { CBOnlineLib.api.getMyStats(id) }
    suspend fun fetchUser() = safeApiCall { CBOnlineLib.onlineV2JsonApi.getMe() }
    suspend fun refreshToken() = safeApiCall { CBOnlineLib.api.refreshToken(prefs.SP_JWT_REFRESH_TOKEN) }
    suspend fun addWishlist(wishList: Wishlist) = safeApiCall { CBOnlineLib.onlineV2JsonApi.addWishlist(wishList) }
    suspend fun removeWishlist(id: String) = safeApiCall { CBOnlineLib.onlineV2JsonApi.removeWishlist(id) }
    suspend fun checkWishlisted(s: String) = safeApiCall { CBOnlineLib.onlineV2JsonApi.checkIfWishlisted(s) }
    suspend fun fetchWishlist() = safeApiCall { CBOnlineLib.onlineV2JsonApi.getWishlist(page = "3") }
    suspend fun fetchBanner() = safeApiCall { CBOnlineLib.hackapi.getBanner() }
}
