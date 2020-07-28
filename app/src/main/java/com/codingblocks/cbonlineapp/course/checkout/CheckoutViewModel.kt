package com.codingblocks.cbonlineapp.course.checkout

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.codingblocks.cbonlineapp.baseclasses.BaseCBViewModel
import com.codingblocks.cbonlineapp.util.extensions.runIO
import com.codingblocks.onlineapi.CBOnlineLib
import com.codingblocks.onlineapi.ResultWrapper
import com.codingblocks.onlineapi.fetchError
import com.codingblocks.onlineapi.safeApiCall
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import org.json.JSONObject

/**
 * @author aggarwalpulkit596
 */
class CheckoutViewModel : BaseCBViewModel() {

    var cartId: String = ""
    val paymentStart = MutableLiveData<Boolean>()
    var cart = MutableLiveData<JsonObject>()
    var map = hashMapOf<String, Any>()
    var paymentMap = hashMapOf<String, String>()
    var creditsApplied = false
    var couponApplied: String = ""

    fun getCart() {
        runIO {
            when (val response = safeApiCall { CBOnlineLib.api.getCart() }) {
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

    fun clearCart() {
        runIO {
            when (val response = safeApiCall { CBOnlineLib.api.clearCart() }) {
                is ResultWrapper.GenericError -> setError(response.error)
                is ResultWrapper.Success -> with(response.value) {
                    if (isSuccessful) {
                        // nothing
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
            when (val response = safeApiCall { CBOnlineLib.api.updateCart(map) }) {
                is ResultWrapper.GenericError -> setError(response.error)
                is ResultWrapper.Success -> with(response.value) {
                    if (isSuccessful) {
                        cart.postValue(body())
                        getCart()
                    } else {
                        errorBody()?.string()?.let {
                            val error = JSONObject(it)
                            val msg: String
                            msg = if (error.getJSONObject("err").has("err")) {
                                error.getJSONObject("err").getString("err")
                            } else
                                error.getJSONObject("err").getString("error")
                            setError(msg)
                        }
                    } ?: setError(fetchError(code()))
                }
            }
        }
    }

    fun capturePayment(function: (status: Boolean) -> Unit) {
        runIO {
            when (val response = safeApiCall { CBOnlineLib.api.capturePayment(paymentMap) }) {
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

    fun addOrder() = liveData(Dispatchers.IO) {
        when (val response = safeApiCall { CBOnlineLib.api.addOrder() }) {
            is ResultWrapper.GenericError -> setError(response.error)
            is ResultWrapper.Success -> with(response.value) {
                if (isSuccessful) {
                    emit(body())
                } else {
                    setError(fetchError(code()))
                }
            }
        }
    }
}
