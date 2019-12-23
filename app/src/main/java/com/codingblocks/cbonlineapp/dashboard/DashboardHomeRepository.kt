package com.codingblocks.cbonlineapp.dashboard

import com.codingblocks.cbonlineapp.AppPrefs
import com.codingblocks.onlineapi.Clients
import com.codingblocks.onlineapi.models.User
import com.codingblocks.onlineapi.safeApiCall

class DashboardHomeRepository(private val prefs: AppPrefs) {

    suspend fun fetchUser() = safeApiCall { Clients.onlineV2JsonApi.getMe() }

    fun insertUser(user: User) {
        with(user) {
            prefs.oneAuthId = oneauthId
            prefs.userId = id
            prefs.userImage = photo ?: "empty"
            prefs.roleId = roleId
            prefs.firstName = firstname
            prefs.lastName = lastname
        }
    }

}
