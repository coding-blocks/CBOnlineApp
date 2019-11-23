package com.codingblocks.cbonlineapp.admin.doubts

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codingblocks.onlineapi.ResultWrapper
import com.codingblocks.onlineapi.models.DoubtsJsonApi
import kotlinx.coroutines.launch

class DoubtsViewModel(private val repo: DoubtRepository) : ViewModel() {

    var listDoubtsResponse: MutableLiveData<List<DoubtsJsonApi>> = MutableLiveData()

    init {
        fetchDoubts()
    }

    fun fetchDoubts() {
        viewModelScope.launch {
            when (val response = repo.getLiveDoubts()) {
                is ResultWrapper.GenericError -> response.code?.let { showError(it) }
                is ResultWrapper.Success -> {
                    with(response.value) {
                        if (isSuccessful)
                            listDoubtsResponse.postValue(body())
                        else {
                            showError(code())
                        }
                    }
                }
            }

        }
    }

    private fun showError(error: Int) {
        //Show Appropriate UI

    }
}
