package com.codingblocks.cbonlineapp.dashboard.home

import androidx.lifecycle.MutableLiveData
import com.codingblocks.cbonlineapp.baseclasses.BaseCBViewModel
import com.codingblocks.cbonlineapp.util.extensions.runIO
import com.codingblocks.onlineapi.ResultWrapper
import com.codingblocks.onlineapi.fetchError
import com.codingblocks.onlineapi.models.Wishlist
import kotlinx.coroutines.Dispatchers

class WishlistViewModel(
    private val repo: WishlistRepository
) : BaseCBViewModel() {

    var wishlist = MutableLiveData<List<Wishlist>>()
    fun fetchWishList() {
        runIO {
            when (val response = repo.fetchWishlist()) {
                is ResultWrapper.GenericError -> {
                    setError(response.error)
                }
                is ResultWrapper.Success -> {
                    if (response.value.isSuccessful) {
                        wishlist.postValue(response.value.body())
                    } else {
                        setError(fetchError(response.value.code()))
                    }
                }
            }
        }
    }
}
