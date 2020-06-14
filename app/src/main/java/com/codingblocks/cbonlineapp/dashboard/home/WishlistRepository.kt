package com.codingblocks.cbonlineapp.dashboard.home

import com.codingblocks.onlineapi.CBOnlineLib
import com.codingblocks.onlineapi.Clients
import com.codingblocks.onlineapi.safeApiCall

class WishlistRepository() {
    suspend fun fetchWishlist()  = safeApiCall { CBOnlineLib.onlineV2JsonApi.getWishlist("course.*","course","100") }
}
