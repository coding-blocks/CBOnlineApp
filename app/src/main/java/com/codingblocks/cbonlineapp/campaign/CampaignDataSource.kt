package com.codingblocks.cbonlineapp.campaign

import androidx.paging.PageKeyedDataSource
import com.codingblocks.onlineapi.CBOnlineLib
import com.codingblocks.onlineapi.ResultWrapper
import com.codingblocks.onlineapi.getMeta
import com.codingblocks.onlineapi.models.Spins
import com.codingblocks.onlineapi.safeApiCall
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class CampaignDataSource(private val scope: CoroutineScope) :
    PageKeyedDataSource<String, Spins>() {

    override fun loadInitial(params: LoadInitialParams<String>, callback: LoadInitialCallback<String, Spins>) {
        scope.launch {
            when (val response = safeApiCall { CBOnlineLib.onlineV2JsonApi.getCampaignLeaderBoard("0", "10") }) {
                is ResultWrapper.Success -> with(response.value) {
                    if (response.value.isSuccessful)
                        response.value.body()?.let {
                            val winnings = it.get()
                            val currentOffSet = getMeta(it.meta, "currentOffset").toString()
                            val nextOffSet = getMeta(it.meta, "nextOffset").toString()
                            callback.onResult(winnings ?: listOf(), currentOffSet, nextOffSet)
                        }
                }
            }
        }
    }

    override fun loadAfter(params: LoadParams<String>, callback: LoadCallback<String, Spins>) {
        scope.launch {
            when (val response = safeApiCall { CBOnlineLib.onlineV2JsonApi.getCampaignLeaderBoard(params.key, "10") }) {
                is ResultWrapper.Success -> with(response.value) {
                    if (response.value.isSuccessful)
                        response.value.body()?.let {
                            val winnings = it.get()
                            val currentOffSet = getMeta(it.meta, "currentOffset").toString()
                            val nextOffSet = getMeta(it.meta, "nextOffset").toString()
                            if (currentOffSet != nextOffSet && nextOffSet != "null") {
                                callback.onResult(winnings ?: listOf(), nextOffSet)
                            }
                        }
                }
            }
        }
    }

    override fun loadBefore(params: LoadParams<String>, callback: LoadCallback<String, Spins>) {
    }

    override fun invalidate() {
        super.invalidate()
        scope.cancel()
    }
}
