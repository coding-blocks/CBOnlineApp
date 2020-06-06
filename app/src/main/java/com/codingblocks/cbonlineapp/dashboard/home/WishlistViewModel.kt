package com.codingblocks.cbonlineapp.dashboard.home

import androidx.lifecycle.liveData
import com.codingblocks.cbonlineapp.baseclasses.BaseCBViewModel
import kotlinx.coroutines.Dispatchers

class WishlistViewModel(
    private val repo: WishlistRepository
) : BaseCBViewModel() {

    fun wishlist() = liveData(Dispatchers.IO) {
        emitSource(repo.fetchWishlist())
    }
}
