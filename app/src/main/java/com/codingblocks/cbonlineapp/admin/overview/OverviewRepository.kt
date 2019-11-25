package com.codingblocks.cbonlineapp.admin.overview

import com.codingblocks.onlineapi.Clients
import com.codingblocks.onlineapi.ResultWrapper
import com.codingblocks.onlineapi.models.DoubtStats
import com.codingblocks.onlineapi.safeApiCall
import retrofit2.Response

class OverviewRepository {

    suspend fun getDoubtStats(userId: String): ResultWrapper<Response<DoubtStats>> {
        return safeApiCall { Clients.api.doubtStats(userId) }
    }

}
