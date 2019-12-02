package com.codingblocks.cbonlineapp.admin

import com.codingblocks.onlineapi.Clients

class AdminRepository {
    suspend fun getDoubtStats(id: String) = Clients.api.doubtStats(id)
}
