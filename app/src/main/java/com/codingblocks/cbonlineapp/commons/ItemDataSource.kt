package com.codingblocks.cbonlineapp.commons

import androidx.paging.PageKeyedDataSource
import com.codingblocks.onlineapi.models.Jobs

class ItemDataSource : PageKeyedDataSource<Int, Jobs>() {

    //the size of a page that we want
    val PAGE_SIZE = 12

    //we will start from the first page which is 1
    private val FIRST_PAGE = 1

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, Jobs>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Jobs>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Jobs>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
