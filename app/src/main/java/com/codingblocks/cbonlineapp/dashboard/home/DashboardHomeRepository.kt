package com.codingblocks.cbonlineapp.dashboard.home

import com.codingblocks.cbonlineapp.AppPrefs
import com.codingblocks.cbonlineapp.database.CourseWithInstructorDao
import com.codingblocks.onlineapi.Clients
import com.codingblocks.onlineapi.models.User
import com.codingblocks.onlineapi.safeApiCall

class DashboardHomeRepository(private val prefs: AppPrefs,
                              private val courseWithInstructorDao: CourseWithInstructorDao) {

    suspend fun fetchUser() = safeApiCall { Clients.onlineV2JsonApi.getMe() }

    suspend fun getToken(grantCode: String) = safeApiCall { Clients.api.getToken(grantCode) }

    fun insertUser(user: User) {
        with(user) {
            prefs.oneAuthId = oneauthId ?: ""
            prefs.userId = id
            prefs.userImage = photo ?: "empty"
            prefs.roleId = roleId
            prefs.firstName = firstname
            prefs.lastName = lastname
        }
    }

    fun getTopRun() = courseWithInstructorDao.getTopRun()


}
