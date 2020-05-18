package com.codingblocks.cbonlineapp.mycourse.codechallenge

import com.codingblocks.onlineapi.Clients
import com.codingblocks.onlineapi.safeApiCall

class CodeChallengeRepository {
    suspend fun fetchCodeChallenge(codeId: Int,contestId: String) = safeApiCall { Clients.onlineV2JsonApi.getCodeChallenge(codeId,contestId) }
}
