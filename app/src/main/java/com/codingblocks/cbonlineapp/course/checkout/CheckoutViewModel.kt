package com.codingblocks.cbonlineapp.course.checkout

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * @author aggarwalpulkit596
 */
class CheckoutViewModel(private val repo: CheckoutRepository) : ViewModel() {
    val response = MutableLiveData<Boolean>()
}
