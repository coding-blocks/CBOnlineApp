package com.codingblocks.cbonlineapp.admin.doubts

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.codingblocks.cbonlineapp.util.extensions.runIO
import com.codingblocks.onlineapi.ErrorStatus
import com.codingblocks.onlineapi.ResultWrapper
import com.codingblocks.onlineapi.fetchError
import com.codingblocks.onlineapi.models.Doubts
import retrofit2.Response

class DoubtsViewModel(private val repo: DoubtRepository) : ViewModel() {

    var listDoubtsResponse: MutableLiveData<List<Doubts>> = MutableLiveData()
    var errorLiveData: MutableLiveData<String> = MutableLiveData()


    fun fetchLiveDoubts() {
        runIO {
            val response = repo.getLiveDoubts()
            assignResponse(response)
        }
    }

    fun fetchMyDoubts(id: String) {
        runIO {
            val response = repo.getMyDoubts(id)
            assignResponse(response)
        }
    }

    fun resolveDoubt(id: String, doubts: Doubts) {
        runIO {
            when (val response = repo.resolveDoubt(id, doubts)) {
                is ResultWrapper.GenericError -> setError(response.error)
                is ResultWrapper.Success -> {
                    if (response.value.isSuccessful)
                        fetchMyDoubts("238594")
                    else {
                        setError(fetchError(response.value.code()))
                    }
                }
            }
        }
    }

    fun acknowledgeDoubt(id: String, doubts: Doubts) {
        runIO {
            when (val response = repo.acknowledgeDoubt(id, doubts)) {
                is ResultWrapper.GenericError -> setError(response.error)
                is ResultWrapper.Success -> {
                    if (response.value.isSuccessful)
                        fetchLiveDoubts()
                    else {
                        setError(fetchError(response.value.code()))
                    }
                }
            }
        }
    }

    private fun assignResponse(response: ResultWrapper<Response<List<Doubts>>>) {
        when (response) {
            is ResultWrapper.GenericError -> setError(response.error)
            is ResultWrapper.Success -> with(response.value) {
                if (isSuccessful)
                    if (body().isNullOrEmpty()) {
                        setError(ErrorStatus.EMPTY_RESPONSE)
                    } else {
                        listDoubtsResponse.postValue(body())
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

