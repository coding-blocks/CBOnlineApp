package com.codingblocks.cbonlineapp.profile

import com.codingblocks.cbonlineapp.util.PreferenceHelper
import com.codingblocks.onlineapi.CBOnlineLib
import com.codingblocks.onlineapi.safeApiCall

/**
 * @author aggarwalpulkit596
 */
class ProfileRepository(
    val prefs: PreferenceHelper
) {

    suspend fun fetchUser() = safeApiCall { CBOnlineLib.onlineV2JsonApi.getMe() }

    suspend fun updateUser(id: String, map: Map<String, String>) = safeApiCall { CBOnlineLib.api.updateUser(id, map) }

    suspend fun refreshToken() = safeApiCall { CBOnlineLib.api.refreshToken(prefs.SP_JWT_REFRESH_TOKEN) }
}
