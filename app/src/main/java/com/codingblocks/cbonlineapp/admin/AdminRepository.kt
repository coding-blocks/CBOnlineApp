package com.codingblocks.cbonlineapp.admin

import com.codingblocks.onlineapi.Clients

class AdminRepository {
    suspend fun getDoubtStats(id: Int) = Clients.api.doubtStats(id)
}
