package com.codingblocks.cbonlineapp.admin.doubts

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.codingblocks.cbonlineapp.baseclasses.BaseCBViewModel
import com.codingblocks.cbonlineapp.util.extensions.runIO
import com.codingblocks.onlineapi.ResultWrapper
import com.codingblocks.onlineapi.fetchError
import com.codingblocks.onlineapi.getMeta
import com.codingblocks.onlineapi.models.Doubts
import com.github.jasminb.jsonapi.JSONAPIDocument
import kotlinx.coroutines.async
import retrofit2.Response

class AdminDoubtsViewModel(private val repo: AdminDoubtRepository) : BaseCBViewModel() {

    var listDoubtsResponse: MutableLiveData<List<Doubts>> = MutableLiveData()
    var nextOffSet: MutableLiveData<Int> = MutableLiveData(-1)
    var prevOffSet: MutableLiveData<Int> = MutableLiveData(-1)
    var barMessage: MutableLiveData<String> = MutableLiveData()

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

    fun acknowledgeDoubt(id: String, doubts: Doubts, myDoubts: String = "") {
        runIO {
            when (val response = repo.acknowledgeDoubt(id, doubts)) {
                is ResultWrapper.GenericError -> setError(response.error)
                is ResultWrapper.Success -> {
                    if (response.value.isSuccessful)
                        if (myDoubts.isEmpty()) {
                            barMessage.postValue("Doubt has been successfully Acknowledged")
                            fetchLiveDoubts()
                        } else {
                            barMessage.postValue("Doubt has been successfully Resolved")
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
                if (isSuccessful) {
                    nextOffSet.postValue(getMeta(body()?.meta, "nextOffset") ?: -1)
                    prevOffSet.postValue(getMeta(body()?.meta, "prevOffset") ?: -1)
                    listDoubtsResponse.postValue(body()?.get())
                } else {
                    setError(fetchError(code()))
                }
            }
        }
    }

    suspend fun requestChat(doubtId: String): String = viewModelScope.async {
        when (val response = repo.getChatId(doubtId)) {
            is ResultWrapper.GenericError -> {
                setError(response.error)
                return@async ""
            }
            is ResultWrapper.Success -> with(response.value) {
                if (isSuccessful)
                    return@async body()?.get("conversationId")?.asString ?: ""
                else {
                    setError(fetchError(response.value.code()))
                    return@async ""
                }
            }
        }
    }.await()
}
