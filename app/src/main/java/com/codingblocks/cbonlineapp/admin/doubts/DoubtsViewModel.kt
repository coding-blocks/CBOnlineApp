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

    init {
        fetchMyDoubts("238594")
    }

    private fun fetchDoubts() {
        runIO {
            val response = repo.getLiveDoubts()
            assignValues(response)
        }
    }

    private fun fetchMyDoubts(id: String) {
        runIO {
            val response = repo.getMyDoubts(id)
            assignValues(response)
        }
    }

    private fun assignValues(response: ResultWrapper<Response<List<Doubts>>>) {
        when (response) {
            is ResultWrapper.GenericError -> showError(response.error)
            is ResultWrapper.Success -> with(response.value) {
                if (isSuccessful)
                    if (body().isNullOrEmpty()) {
                        showError(ErrorStatus.EMPTY_RESPONSE)
                    } else {
                        listDoubtsResponse.postValue(body())
                    }
                else {
                    showError(fetchError(code()))
                }
            }
        }
    }

    private fun showError(error: String) {
        //Show Appropriate UI
        errorLiveData.postValue(error)

    }
}

