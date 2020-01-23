package com.codingblocks.cbonlineapp.tracks

import com.codingblocks.onlineapi.Clients
import com.codingblocks.onlineapi.safeApiCall

/**
 * @author aggarwalpulkit596
 */
class TracksRepository {
    suspend fun getTracks(id: String) = safeApiCall { Clients.onlineV2JsonApi.getTrack(id) }

    suspend fun getTrackCourses(id: String) = safeApiCall { Clients.onlineV2JsonApi.getTrackCourses(id) }
}
