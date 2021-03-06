package com.codingblocks.cbonlineapp.admin.overview

import com.codingblocks.onlineapi.CBOnlineLib
import com.codingblocks.onlineapi.ResultWrapper
import com.codingblocks.onlineapi.models.DoubtLeaderBoard
import com.codingblocks.onlineapi.models.DoubtStats
import com.codingblocks.onlineapi.safeApiCall
import com.github.jasminb.jsonapi.JSONAPIDocument
import retrofit2.Response

class AdminOverviewRepository {

    suspend fun getDoubtStats(userId: String): ResultWrapper<Response<DoubtStats>> {
        return safeApiCall { CBOnlineLib.api.doubtStats(userId) }
    }

    suspend fun getLeaderBoard(offSet: Int): ResultWrapper<Response<JSONAPIDocument<List<DoubtLeaderBoard>>>> {
        return safeApiCall { CBOnlineLib.onlineV2JsonApi.getLeaderBoard(offset = offSet) }
    }
}
