package com.codingblocks.cbonlineapp.profile

import com.codingblocks.cbonlineapp.util.PreferenceHelper
import com.codingblocks.onlineapi.Clients
import com.codingblocks.onlineapi.safeApiCall

/**
 * @author aggarwalpulkit596
 */
class ProfileRepository(
    val prefs: PreferenceHelper
) {

    suspend fun fetchUser() = safeApiCall { Clients.onlineV2JsonApi.getMe() }

    suspend fun updateUser(id: String, map: Map<String, String>) = safeApiCall { Clients.api.updateUser(id, map) }
}
