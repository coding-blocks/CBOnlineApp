package com.codingblocks.cbonlineapp.dashboard.home

import androidx.paging.PageKeyedDataSource
import com.codingblocks.onlineapi.*
import com.codingblocks.onlineapi.models.Wishlist
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class WishlistDataSource(private val scope: CoroutineScope, private val pageSize: String) :
    PageKeyedDataSource<String, Wishlist>() {

    override fun loadInitial(params: LoadInitialParams<String>, callback: LoadInitialCallback<String, Wishlist>) {
        scope.launch {
            when (val response = safeApiCall { CBOnlineLib.onlineV2JsonApi.getWishlist(page = pageSize) }) {
                is ResultWrapper.Success -> with(response.value) {
                    if (isSuccessful)
                        body()?.let {
                            val wishlist = it.get()
                            val currentOffSet = getMeta(it.meta, "currentOffset").toString()
                            val nextOffSet = getMeta(it.meta, "nextOffset").toString()
                            callback.onResult(wishlist ?: listOf(), currentOffSet, nextOffSet)
                        }
                }
            }
        }
    }

    override fun loadAfter(params: LoadParams<String>, callback: LoadCallback<String, Wishlist>) {
        scope.launch {
            when (val response = safeApiCall { CBOnlineLib.onlineV2JsonApi.getWishlist(offset = params.key, page = pageSize) }) {
                is ResultWrapper.Success -> with(response.value) {
                    if (isSuccessful)
                        body()?.let {
                            val wishlist = it.get()
                            val currentOffSet = getMeta(it.meta, "currentOffset").toString()
                            val nextOffSet = getMeta(it.meta, "nextOffset").toString()
                            if (nextOffSet != currentOffSet && nextOffSet != "null") {
                                callback.onResult(wishlist ?: listOf(), nextOffSet)
                            }
                        }
                }
            }
        }
    }

    override fun loadBefore(params: LoadParams<String>, callback: LoadCallback<String, Wishlist>) {
    }

    override fun invalidate() {
        super.invalidate()
        scope.cancel()
    }
}
