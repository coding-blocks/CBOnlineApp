package com.codingblocks.cbonlineapp.dashboard.home

import androidx.lifecycle.distinctUntilChanged
import com.codingblocks.cbonlineapp.database.CourseWithInstructorDao
import com.codingblocks.cbonlineapp.database.RunPerformanceDao
import com.codingblocks.cbonlineapp.database.models.RunPerformance
import com.codingblocks.cbonlineapp.util.PreferenceHelper
import com.codingblocks.onlineapi.Clients
import com.codingblocks.onlineapi.models.PerformanceResponse
import com.codingblocks.onlineapi.models.User
import com.codingblocks.onlineapi.safeApiCall

class DashboardHomeRepository(
    private val courseWithInstructorDao: CourseWithInstructorDao,
    private val runPerformanceDao: RunPerformanceDao,
    val prefs: PreferenceHelper

) {

    suspend fun fetchUser() = safeApiCall { Clients.onlineV2JsonApi.getMe() }

    suspend fun getToken(grantCode: String) = safeApiCall { Clients.api.getToken(grantCode) }

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

    suspend fun getStats(id: String) = safeApiCall { Clients.api.getMyStats(id) }
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

    fun getTopRun() = courseWithInstructorDao.getTopRun().distinctUntilChanged()
    fun getRunStats(query: String?) = query?.let { runPerformanceDao.getPerformance(it) }
}
