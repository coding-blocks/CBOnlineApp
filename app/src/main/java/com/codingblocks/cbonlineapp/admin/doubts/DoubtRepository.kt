package com.codingblocks.cbonlineapp.admin.doubts

import com.codingblocks.onlineapi.Clients
import com.codingblocks.onlineapi.ResultWrapper
import com.codingblocks.onlineapi.models.DoubtsJsonApi
import com.codingblocks.onlineapi.safeApiCall
import kotlinx.coroutines.Dispatchers
import retrofit2.Response

class DoubtRepository {

    suspend fun getLiveDoubts(): ResultWrapper<Response<List<DoubtsJsonApi>>> {
        return safeApiCall(Dispatchers.IO) { Clients.onlineV2JsonApi.getLiveDoubts() }
    }
}
