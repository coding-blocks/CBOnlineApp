package com.codingblocks.cbonlineapp.course.checkout

import androidx.lifecycle.MutableLiveData
import com.codingblocks.cbonlineapp.baseclasses.BaseCBViewModel
import com.codingblocks.cbonlineapp.util.extensions.runIO
import com.codingblocks.onlineapi.Clients
import com.codingblocks.onlineapi.ResultWrapper
import com.codingblocks.onlineapi.fetchError
import com.codingblocks.onlineapi.safeApiCall
import com.google.gson.JsonObject

/**
 * @author aggarwalpulkit596
 */
class CheckoutViewModel : BaseCBViewModel() {

    val paymentStart = MutableLiveData<Boolean>()
    var cart = MutableLiveData<JsonObject>()
    var map = hashMapOf<String, Any>()
    var paymentMap = hashMapOf<String, String>()

    fun getCart() {
        runIO {
            when (val response = safeApiCall { Clients.api.getCart() }) {
                is ResultWrapper.GenericError -> setError(response.error)
                is ResultWrapper.Success -> with(response.value) {
                    if (isSuccessful) {
                        cart.postValue(body())
                    } else {
                        cart.postValue(null)
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

    fun capturePayment(function: (status: Boolean) -> Unit) {
        runIO {
            when (val response = safeApiCall { Clients.api.capturePayment(paymentMap) }) {
                is ResultWrapper.GenericError -> setError(response.error)
                is ResultWrapper.Success -> with(response.value) {
                    if (isSuccessful) {
                        cart.postValue(body())
                        function(true)
                    } else {
                        setError(fetchError(code()))
                        function(false)
                    }
                }
            }
        }
    }
}
