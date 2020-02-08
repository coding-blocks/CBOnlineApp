package com.codingblocks.cbonlineapp.admin.overview

import androidx.lifecycle.MutableLiveData
import com.codingblocks.cbonlineapp.baseclasses.BaseCBViewModel
import com.codingblocks.cbonlineapp.util.PreferenceHelper
import com.codingblocks.cbonlineapp.util.extensions.runIO
import com.codingblocks.onlineapi.ErrorStatus
import com.codingblocks.onlineapi.ResultWrapper
import com.codingblocks.onlineapi.fetchError
import com.codingblocks.onlineapi.getMeta
import com.codingblocks.onlineapi.models.DoubtLeaderBoard
import com.codingblocks.onlineapi.models.DoubtStats
import com.github.jasminb.jsonapi.JSONAPIDocument
import retrofit2.Response

class AdminOverviewViewModel(private val repo: AdminOverviewRepository, val prefs: PreferenceHelper) : BaseCBViewModel() {

    var doubtStats: MutableLiveData<DoubtStats> = MutableLiveData()
    var listLeaderboard: MutableLiveData<List<DoubtLeaderBoard>> = MutableLiveData()
    var nextOffSet: MutableLiveData<Int> = MutableLiveData(-1)
    var prevOffSet: MutableLiveData<Int> = MutableLiveData(-1)

    init {
        fetchLeaderBoard()
    }

    fun fetchDoubtStats(userId: String = prefs.SP_USER_ID) {
        runIO {
            val response = repo.getDoubtStats(userId)
            assignResponse(response)
        }
    }

    fun fetchLeaderBoard(offSet: Int = 0) {
        runIO {
            val response = repo.getLeaderBoard(offSet)
            assignLeaderBoardResponse(response)
        }
    }

    private fun assignLeaderBoardResponse(response: ResultWrapper<Response<JSONAPIDocument<List<DoubtLeaderBoard>>>>) {
        when (response) {
            is ResultWrapper.GenericError -> setError(response.error)
            is ResultWrapper.Success -> with(response.value) {
                if (isSuccessful)
                    if (body()?.get().isNullOrEmpty()) {
                        setError(ErrorStatus.EMPTY_RESPONSE)
                    } else {
                        nextOffSet.postValue(getMeta(body()?.meta, "nextOffset") ?: -1)
                        prevOffSet.postValue(getMeta(body()?.meta, "prevOffset") ?: -1)
                        listLeaderboard.postValue(body()?.get())
                    }
                else {
                    setError(fetchError(code()))
                }
            }
        }
    }

    private fun assignResponse(response: ResultWrapper<Response<DoubtStats>>) {
        when (response) {
            is ResultWrapper.GenericError -> setError(response.error)
            is ResultWrapper.Success -> with(response.value) {
                if (isSuccessful)
                    doubtStats.postValue(body())
                else {
                    setError(fetchError(code()))
                }
            }
        }
    }
}
