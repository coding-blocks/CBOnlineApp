package com.codingblocks.cbonlineapp.campaign

import com.codingblocks.onlineapi.Clients
import com.codingblocks.onlineapi.safeApiCall

class CampaignRepository {

    suspend fun getSpinStats() = safeApiCall { Clients.api.spinStats() }

    suspend fun drawSpin() = safeApiCall { Clients.api.drawSpin() }


}
