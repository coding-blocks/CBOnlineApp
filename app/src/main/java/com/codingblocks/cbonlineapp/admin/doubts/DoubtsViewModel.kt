package com.codingblocks.cbonlineapp.admin.doubts

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.codingblocks.cbonlineapp.util.extensions.runIO
import com.codingblocks.onlineapi.ErrorStatus
import com.codingblocks.onlineapi.ResultWrapper
import com.codingblocks.onlineapi.fetchError
import com.codingblocks.onlineapi.getMeta
import com.codingblocks.onlineapi.models.Doubts
import com.github.jasminb.jsonapi.JSONAPIDocument
import retrofit2.Response

class DoubtsViewModel(private val repo: DoubtRepository) : ViewModel() {

    var listDoubtsResponse: MutableLiveData<List<Doubts>> = MutableLiveData()
    var errorLiveData: MutableLiveData<String> = MutableLiveData()
    var nextOffSet: MutableLiveData<Int> = MutableLiveData(0)
    var prevOffSet: MutableLiveData<Int> = MutableLiveData(0)


    fun fetchLiveDoubts(offSet: Int = 0) {
        runIO {
            val response = repo.getLiveDoubts(offSet)
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

    fun acknowledgeDoubt(id: String, doubts: Doubts, myDoubts: String = "") {
        runIO {
            when (val response = repo.acknowledgeDoubt(id, doubts)) {
                is ResultWrapper.GenericError -> setError(response.error)
                is ResultWrapper.Success -> {
                    if (response.value.isSuccessful)
                        if (myDoubts.isEmpty())
                            fetchLiveDoubts()
                        else {
                            fetchMyDoubts(myDoubts)
                        }
                    else {
                        setError(fetchError(response.value.code()))
                    }
                }
            }
        }
    }

    private fun assignResponse(response: ResultWrapper<Response<JSONAPIDocument<List<Doubts>>>>) {
        when (response) {
            is ResultWrapper.GenericError -> setError(response.error)
            is ResultWrapper.Success -> with(response.value) {
                if (isSuccessful)
                    if (body()?.get().isNullOrEmpty()) {
                        setError(ErrorStatus.EMPTY_RESPONSE)
                    } else {
                        nextOffSet.postValue(getMeta(body()?.meta, "nextOffset"))
                        prevOffSet.postValue(getMeta(body()?.meta, "prevOffset"))
                        listDoubtsResponse.postValue(body()?.get())
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

