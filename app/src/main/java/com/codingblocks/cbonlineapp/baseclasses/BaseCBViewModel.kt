package com.codingblocks.cbonlineapp.baseclasses

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * @author aggarwalpulkit596
 */
abstract class BaseCBViewModel : ViewModel() {
    var errorLiveData: MutableLiveData<String> = MutableLiveData()

    fun setError(error: String) {
        errorLiveData.postValue(error)
    }
}

enum class STATE {
    SUCCESS,
    ERROR,
    LOADING
}
