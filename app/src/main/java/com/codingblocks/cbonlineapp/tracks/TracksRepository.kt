package com.codingblocks.cbonlineapp.tracks

import com.codingblocks.onlineapi.Clients
import com.codingblocks.onlineapi.safeApiCall

/**
 * @author aggarwalpulkit596
 */
class TracksRepository {
    suspend fun getTrack(id: String) = safeApiCall { Clients.onlineV2JsonApi.getTrack(id) }

    suspend fun getTrackCourses(id: String) = safeApiCall { Clients.onlineV2JsonApi.getTrackCourses(id) }

    suspend fun getTracks() = safeApiCall { Clients.onlineV2JsonApi.getTracks() }

    suspend fun getProfessions() = safeApiCall { Clients.onlineV2JsonApi.getProfessions() }
    suspend fun getRecommendedTrack(map: HashMap<String, String>) = safeApiCall { Clients.onlineV2JsonApi.getRecommendedTrack(map) }
    suspend fun generateLead(body: HashMap<String, Any>) = safeApiCall { Clients.api.generateLead(body) }
}
