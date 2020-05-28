package com.codingblocks.cbonlineapp.auth

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.liveData
import com.codingblocks.cbonlineapp.baseclasses.BaseCBViewModel
import com.codingblocks.cbonlineapp.util.extensions.runIO
import com.codingblocks.cbonlineapp.util.savedStateValue
import com.codingblocks.onlineapi.ResultWrapper
import java.util.HashMap
import kotlinx.coroutines.Dispatchers
import okhttp3.ResponseBody
import org.json.JSONObject

const val PHONE_NUMBER = "phoneNumber"
const val ID = "id"

class AuthViewModel(
    handle: SavedStateHandle,
    private val repo: AuthRepository
) : BaseCBViewModel() {

    var mobile by savedStateValue<String>(handle, PHONE_NUMBER)
    var uniqueId by savedStateValue<String>(handle, ID)

    fun fetchToken(grantCode: String) = liveData<Boolean>(Dispatchers.IO) {
        when (val response = repo.getToken(grantCode)) {
            is ResultWrapper.GenericError -> setError(response.error)
            is ResultWrapper.Success -> {
                if (response.value.isSuccessful)
                    response.value.body()?.let {
                        repo.saveKeys(it)
                        emit(true)
                    }
            }
        }
    }

    fun sendOtp(dialCode: String) {
        runIO {
            when (val response = repo.sendOtp(dialCode, mobile!!)) {
                is ResultWrapper.GenericError -> setError(response.error)
                is ResultWrapper.Success -> {
                    if (response.value.isSuccessful)
                        response.value.body()?.let {
                            uniqueId = it.get("id").asString
                        }
                    else {
                        response.value.errorBody()?.let { parseError(it) }
                    }
                }
            }
        }
    }

    fun verifyOtp(otp: String) {
        runIO {
            if (uniqueId.isNullOrEmpty()) {
                errorLiveData.postValue("There was some error.Please try Again!")
            } else {
                when (val response = repo.verifyOtp(otp, uniqueId!!)) {
                    is ResultWrapper.GenericError -> setError(response.error)
                    is ResultWrapper.Success -> {
                        if (response.value.isSuccessful)
                            response.value.body()?.let {
                                uniqueId = it.get("id").asString
                                val status = it.get("status")
                                if (status != null && status.asString == "verified") {
                                    findUser(hashMapOf("verifiedmobile" to mobile!!))
                                }
                            }
                        else {
                            response.value.errorBody()?.let { parseErrorBody(it) }
                        }
                    }
                }
            }
        }
    }

    fun loginWithEmail(name: String, password: String) = liveData<Boolean>(Dispatchers.IO) {
        when (val response = repo.loginWithEmail(name, password)) {
            is ResultWrapper.GenericError -> setError(response.error)
            is ResultWrapper.Success -> {
                if (response.value.isSuccessful)
                    response.value.body()?.let {
                        repo.saveKeys(it)
                        emit(true)
                    }
                else {
                    response.value.errorBody()?.let { parseErrorBody(it) }
                }
            }
        }
    }

    private fun findUser(userMap: HashMap<String, String>) {
        runIO {
            when (val response = repo.findUser(userMap)) {
                is ResultWrapper.GenericError -> setError(response.error)
                is ResultWrapper.Success -> {
                    if (response.value.isSuccessful)
                        response.value.body()?.let {
                            uniqueId = it.get("id").asString
                        }
                    else {
                        response.value.errorBody()?.let { parseError(it) }
                    }
                }
            }
        }
    }

    private fun parseErrorBody(errorBody: ResponseBody) {
        val jObjError = JSONObject(errorBody.string())
        val message = jObjError.getJSONArray("errors").getJSONObject(0).getString("description")
        errorLiveData.postValue(message)
    }

    private fun parseError(errorBody: ResponseBody) {
        val jObjError = JSONObject(errorBody.string())
        val message = jObjError.getString("message")
        errorLiveData.postValue(message)
    }
}
