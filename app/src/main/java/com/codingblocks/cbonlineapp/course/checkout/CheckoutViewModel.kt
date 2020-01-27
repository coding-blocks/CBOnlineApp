package com.codingblocks.cbonlineapp.course.checkout

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.codingblocks.cbonlineapp.util.extensions.runIO
import com.codingblocks.onlineapi.Clients
import com.codingblocks.onlineapi.ResultWrapper
import com.codingblocks.onlineapi.fetchError
import com.codingblocks.onlineapi.safeApiCall
import com.google.gson.JsonObject

/**
 * @author aggarwalpulkit596
 */
class CheckoutViewModel : ViewModel() {

    var errorLiveData = MutableLiveData<String>()
    var cart = MutableLiveData<JsonObject>()
    var map = hashMapOf<String, String>()
    var paymentMap = hashMapOf<String, String>()

    fun getCart() {
        runIO {
            when (val response = safeApiCall { Clients.api.getCart() }) {
                is ResultWrapper.GenericError -> setError(response.error)
                is ResultWrapper.Success -> with(response.value) {
                    if (isSuccessful) {
                        cart.postValue(body())
                    } else {
                        setError(fetchError(code()))
                    }
                }
            }
        }
    }

    fun updateCart() {
        runIO {
            when (val response = safeApiCall { Clients.api.updateCart(map) }) {
                is ResultWrapper.GenericError -> setError(response.error)
                is ResultWrapper.Success -> with(response.value) {
                    if (isSuccessful) {
                        cart.postValue(body())
                    } else {
                        setError(fetchError(code()))
                    }
                }
            }
        }
    }

    private fun setError(error: String) {
        errorLiveData.postValue(error)
    }

    fun capturePayment(function: () -> Unit) {
        runIO {
            when (val response = safeApiCall { Clients.api.capturePayment(paymentMap) }) {
                is ResultWrapper.GenericError -> setError(response.error)
                is ResultWrapper.Success -> with(response.value) {
                    if (isSuccessful) {
                        cart.postValue(body())
                        function()
                    } else {
                        setError(fetchError(code()))
                    }
                }
            }
        }
    }
}
