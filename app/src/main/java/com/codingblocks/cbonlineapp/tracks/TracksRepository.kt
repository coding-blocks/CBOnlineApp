package com.codingblocks.cbonlineapp.tracks

import com.codingblocks.onlineapi.CBOnlineLib
import com.codingblocks.onlineapi.safeApiCall

/**
 * @author aggarwalpulkit596
 */
class TracksRepository {
    suspend fun getTrack(id: String) = safeApiCall { CBOnlineLib.onlineV2JsonApi.getTrack(id) }

    suspend fun getTrackCourses(id: String) = safeApiCall { CBOnlineLib.onlineV2JsonApi.getTrackCourses(id) }

    suspend fun getTracks() = safeApiCall { CBOnlineLib.onlineV2JsonApi.getTracks() }

    suspend fun getProfessions() = safeApiCall { CBOnlineLib.onlineV2JsonApi.getProfessions() }
    suspend fun getRecommendedTrack(map: HashMap<String, String>) = safeApiCall { CBOnlineLib.onlineV2JsonApi.getRecommendedTrack(map) }
    suspend fun generateLead(body: HashMap<String, Any>) = safeApiCall { CBOnlineLib.api.generateLead(body) }
}
