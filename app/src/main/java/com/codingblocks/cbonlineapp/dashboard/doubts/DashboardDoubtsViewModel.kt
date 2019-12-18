package com.codingblocks.cbonlineapp.dashboard.doubts

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.codingblocks.cbonlineapp.util.extensions.runIO
import com.codingblocks.onlineapi.models.Doubts

class DashboardDoubtsViewModel : ViewModel() {


    var listDoubtsResponse: MutableLiveData<List<Doubts>> = MutableLiveData()
    var errorLiveData: MutableLiveData<String> = MutableLiveData()
    var nextOffSet: MutableLiveData<Int> = MutableLiveData(-1)
    var prevOffSet: MutableLiveData<Int> = MutableLiveData(-1)
    var barMessage: MutableLiveData<String> = MutableLiveData()


    fun fetchDoubts() {
        runIO {
            listDoubtsResponse.postValue(emptyList())
        }
    }

}
