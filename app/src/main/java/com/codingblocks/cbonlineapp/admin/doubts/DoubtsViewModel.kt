package com.codingblocks.cbonlineapp.admin.doubts

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codingblocks.onlineapi.ErrorStatus
import com.codingblocks.onlineapi.ResultWrapper
import com.codingblocks.onlineapi.fetchError
import com.codingblocks.onlineapi.models.DoubtsJsonApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DoubtsViewModel(private val repo: DoubtRepository) : ViewModel() {

    var listDoubtsResponse: MutableLiveData<List<DoubtsJsonApi>> = MutableLiveData()
    var errorLiveData: MutableLiveData<String> = MutableLiveData()

    init {
        fetchDoubts()
    }

    private fun fetchDoubts() {
        viewModelScope.launch(Dispatchers.IO) {
            when (val response = repo.getLiveDoubts()) {
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
    }


    private fun showError(error: String) {
        //Show Appropriate UI
        errorLiveData.postValue(error)

    }
}
