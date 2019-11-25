package com.codingblocks.cbonlineapp.admin.overview

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.codingblocks.cbonlineapp.util.extensions.runIO
import com.codingblocks.onlineapi.ErrorStatus
import com.codingblocks.onlineapi.ResultWrapper
import com.codingblocks.onlineapi.fetchError
import com.codingblocks.onlineapi.models.DoubtStats
import retrofit2.Response

class AdminOverviewViewModel(private val repo: OverviewRepository) : ViewModel() {

    var doubtStats: MutableLiveData<DoubtStats> = MutableLiveData()
    var errorLiveData: MutableLiveData<String> = MutableLiveData()
    var nextOffSet: MutableLiveData<Int> = MutableLiveData()
    var prevOffSet: MutableLiveData<Int> = MutableLiveData()


    fun fetchDoubtStats(userId: String = "238594") {
        runIO {
            val response = repo.getDoubtStats(userId)
            assignResponse(response)
        }
    }

    private fun assignResponse(response: ResultWrapper<Response<DoubtStats>>) {
        when (response) {
            is ResultWrapper.GenericError -> setError(response.error)
            is ResultWrapper.Success -> with(response.value) {
                if (isSuccessful)
                    if (body() == null) {
                        setError(ErrorStatus.EMPTY_RESPONSE)
                    } else {
                        doubtStats.postValue(body())
                    }
                else {
                    setError(fetchError(code()))
                }
            }
        }
    }

    private fun setError(error: String) {
        errorLiveData.postValue(error)
    }

}
