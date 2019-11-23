package com.codingblocks.cbonlineapp.admin.overview

import com.codingblocks.onlineapi.Clients

class OverviewRepository {

    suspend fun getStats(user: String) = Clients.api.doubtStats(user)
}
