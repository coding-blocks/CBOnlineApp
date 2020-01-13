package com.codingblocks.cbonlineapp.dashboard.home

import com.codingblocks.cbonlineapp.database.CourseWithInstructorDao
import com.codingblocks.cbonlineapp.database.RunPerformanceDao
import com.codingblocks.cbonlineapp.database.models.RunPerformance
import com.codingblocks.onlineapi.Clients
import com.codingblocks.onlineapi.models.PerformanceResponse
import com.codingblocks.onlineapi.models.User
import com.codingblocks.onlineapi.safeApiCall

class DashboardHomeRepository(
    private val courseWithInstructorDao: CourseWithInstructorDao,
    private val runPerformanceDao: RunPerformanceDao

) {

    suspend fun fetchUser() = safeApiCall { Clients.onlineV2JsonApi.getMe() }

    suspend fun getToken(grantCode: String) = safeApiCall { Clients.api.getToken(grantCode) }

    fun insertUser(user: User) {
        with(user) {
            //            prefs.oneAuthId = oneauthId ?: ""
//            prefs.userId = id
//            prefs.userImage = photo ?: "empty"
//            prefs.roleId = roleId
//            prefs.firstName = firstname
//            prefs.lastName = lastname
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

    fun getTopRun() = courseWithInstructorDao.getTopRun()
    fun getRunStats(query: String?) = query?.let { runPerformanceDao.getPerformance(it) }
}
