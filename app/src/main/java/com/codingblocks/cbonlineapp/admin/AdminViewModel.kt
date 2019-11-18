package com.codingblocks.cbonlineapp.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import kotlinx.coroutines.Dispatchers

class AdminViewModel(private val repository: AdminRepository) : ViewModel() {


    fun doubtStats(id: Int) = liveData(Dispatchers.IO) {
        val retrivedStats = repository.getDoubtStats(id)
        emit(retrivedStats)
    }
}
