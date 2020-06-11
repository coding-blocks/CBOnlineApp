package com.codingblocks.cbonlineapp.admin.doubts

import com.codingblocks.onlineapi.CBOnlineLib
import com.codingblocks.onlineapi.ResultWrapper
import com.codingblocks.onlineapi.models.Doubts
import com.codingblocks.onlineapi.safeApiCall
import com.github.jasminb.jsonapi.JSONAPIDocument
import com.google.gson.JsonObject
import retrofit2.Response

class AdminDoubtRepository {

    suspend fun getLiveDoubts(offSet: Int): ResultWrapper<Response<JSONAPIDocument<List<Doubts>>>> {
        return safeApiCall { CBOnlineLib.onlineV2JsonApi.getLiveDoubts(offset = offSet) }
    }

    suspend fun getMyDoubts(acknowledgedId: String): ResultWrapper<Response<JSONAPIDocument<List<Doubts>>>> {
        return safeApiCall { CBOnlineLib.onlineV2JsonApi.getMyDoubts(acknowledgedId = acknowledgedId) }
    }

    suspend fun acknowledgeDoubt(doubtId: String, doubt: Doubts): ResultWrapper<Response<List<Doubts>>> {
        return safeApiCall { CBOnlineLib.onlineV2JsonApi.acknowledgeDoubt(doubtId, doubt) }
    }

    suspend fun getChatId(doubtId: String): ResultWrapper<Response<JsonObject>> {
        return safeApiCall { CBOnlineLib.api.getChatId(doubtId) }
    }
}
