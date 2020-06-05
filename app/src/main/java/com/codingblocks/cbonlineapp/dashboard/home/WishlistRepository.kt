package com.codingblocks.cbonlineapp.dashboard.home

import com.codingblocks.onlineapi.Clients
import com.codingblocks.onlineapi.safeApiCall

public class WishlistRepository {
    suspend fun fetchWishlist() = safeApiCall { Clients.onlineV2JsonApi.getWishlist("course.*","course","100") }
}
