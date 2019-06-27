package com.codingblocks.cbonlineapp.viewmodels

import androidx.lifecycle.ViewModel
import com.codingblocks.cbonlineapp.extensions.getDate
import com.codingblocks.cbonlineapp.extensions.retrofitCallback
import com.codingblocks.onlineapi.Clients

class JobsViewModel(

) : ViewModel() {

    fun getJobs() {
        Clients.onlineV2JsonApi.getJobs(
            getDate(),
            getDate()
        ).enqueue(retrofitCallback { throwable, response ->
            response?.body().let {
                if (response?.isSuccessful  == true){

                }
            }
        })
    }

}
