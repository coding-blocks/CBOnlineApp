package com.codingblocks.cbonlineapp.dashboard.home

import com.codingblocks.cbonlineapp.database.WishlistDao

class WishlistRepository(
    val wishlistDao: WishlistDao
) {
    fun fetchWishlist() = wishlistDao.getAllWishlists()
}
