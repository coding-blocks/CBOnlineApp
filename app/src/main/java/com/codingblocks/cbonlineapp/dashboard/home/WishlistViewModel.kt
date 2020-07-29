package com.codingblocks.cbonlineapp.dashboard.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.codingblocks.cbonlineapp.baseclasses.BaseCBViewModel
import com.codingblocks.onlineapi.models.Wishlist

class WishlistViewModel() : BaseCBViewModel() {

    private var wishlist: LiveData<PagedList<Wishlist>>
    init {
        val config = PagedList.Config.Builder()
            .setPageSize(10)
            .setEnablePlaceholders(true)
            .build()
        wishlist = initializedPagedListBuilder(config).build()
    }
    fun fetchWishList(): LiveData<PagedList<Wishlist>> = wishlist

    private fun initializedPagedListBuilder(config: PagedList.Config):
        LivePagedListBuilder<String, Wishlist> {

            val dataSourceFactory = object : DataSource.Factory<String, Wishlist>() {
                override fun create(): DataSource<String, Wishlist> {
                    return WishlistDataSource(viewModelScope, "10")
                }
            }
            return LivePagedListBuilder(dataSourceFactory, config)
        }
}
