package com.codingblocks.cbonlineapp.auth

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.codingblocks.cbonlineapp.baseclasses.BaseCBViewModel
import com.codingblocks.cbonlineapp.util.extensions.runIO
import com.codingblocks.cbonlineapp.util.extensions.savedStateValue
import com.codingblocks.onlineapi.ResultWrapper
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject

const val PHONE_NUMBER = "phoneNumber"
const val ID = "id"
const val EMAIL = "email"

class AuthViewModel(
    handle: SavedStateHandle,
    private val repo: AuthRepository
) : BaseCBViewModel() {

    val isLoggedIn = MutableLiveData<Boolean>()
    val account = MutableLiveData<AccountStates>()

    var mobile by savedStateValue<String>(handle, PHONE_NUMBER)
    private var uniqueId by savedStateValue<String>(handle, ID)
    var dialCode by savedStateValue<String>(handle, "dialCode")
    var email by savedStateValue<String>(handle, EMAIL)

    fun fetchToken(grantCode: String) {
        runIO {
            when (val response = repo.getToken(grantCode)) {
                is ResultWrapper.GenericError -> setError(response.error)
                is ResultWrapper.Success -> {
                    if (response.value.isSuccessful)
                        response.value.body()?.let {
                            repo.saveKeys(it)
                            isLoggedIn.postValue(true)
                        }
                }
            }
        }
    }

    fun sendOtp() {
        runIO {
            when (val response = repo.sendOtp(dialCode!!, mobile!!)) {
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
                when (val response = repo.verifyOtp(otp.toInt(), uniqueId!!)) {
                    is ResultWrapper.GenericError -> setError(response.error)
                    is ResultWrapper.Success -> {
                        if (response.value.isSuccessful)
                            response.value.body()?.let {
                                errorLiveData.postValue(null)
                                uniqueId = it.get("id").asString
                                val status = it.get("status")
                                if (status != null && status.asString == "verified") {
                                    findUser(hashMapOf("verifiedmobile" to "$dialCode-$mobile"))
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

    fun loginWithEmail(name: String, password: String) {
        runIO {
            when (val response = repo.loginWithEmail(name, password)) {
                is ResultWrapper.GenericError -> setError(response.error)
                is ResultWrapper.Success -> {
                    if (response.value.isSuccessful)
                        response.value.body()?.let {
                            errorLiveData.postValue(null)
                            repo.saveKeys(it)
                            isLoggedIn.postValue(true)
                            if (!uniqueId.isNullOrEmpty())
                                repo.verifyMobileUsingClaim(uniqueId!!)
                        }
                    else {
                        if (response.value.code() == 500)
                            errorLiveData.postValue("Incorrect email or password")
                        else
                            response.value.errorBody()?.let { parseErrorBody(it) }
                    }
                }
            }
        }
    }

    fun findUser(userMap: HashMap<String, String>) {
        runIO {
            when (val response = repo.findUser(userMap)) {
                is ResultWrapper.GenericError -> setError(response.error)
                is ResultWrapper.Success -> {
                    if (response.value.isSuccessful)
                        when {
                            userMap.containsKey("verifiedmobile") -> {
                                loginUserWithClaim()
                            }
                            userMap.containsKey("verifiedemail") -> {
                                account.postValue(AccountStates.EXITS)
                            }
                            userMap.containsKey("email") -> {
                                account.postValue(AccountStates.EXITS)
                            }
                        } else {
                        if (response.value.code() == 404)
                            when {
                                userMap.containsKey("verifiedmobile") -> {
                                    account.postValue(AccountStates.NUMBER_NOT_VERIFIED)
                                }
                                userMap.containsKey("verifiedemail") -> {
                                    findUser(hashMapOf("email" to email!!))
                                    account.postValue(AccountStates.EMAIL_NOT_VERIFIED)
                                }
                                userMap.containsKey("email") -> {
                                    account.postValue(AccountStates.DO_NOT_EXIST)
                                }
                            } else
                            response.value.errorBody()?.let { parseErrorBody(it) }
                    }
                }
            }
        }
    }

    private fun loginUserWithClaim() {
        runIO {
            when (val response = repo.loginWithClaim(uniqueId!!)) {
                is ResultWrapper.GenericError -> setError(response.error)
                is ResultWrapper.Success -> {
                    if (response.value.isSuccessful)
                        response.value.body()?.let {
                            repo.saveKeys(it)
                            isLoggedIn.postValue(true)
                        }
                    else {
                        if (response.value.code() != 404)
                            response.value.errorBody()?.let { parseError(it) }
                    }
                }
            }
        }
    }

    private fun parseErrorBody(errorBody: ResponseBody) {
        try {
            val jObjError = JSONObject(errorBody.string())
            val message = jObjError.getJSONArray("errors").getJSONObject(0).getString("description")
            errorLiveData.postValue(message)
        } catch (je: JSONException) {
            errorLiveData.postValue("Try Again!!!")
        }
    }

    private fun parseError(errorBody: ResponseBody, key: String = "message") {
        val jObjError = JSONObject(errorBody.string())
        val message = if (jObjError.has(key)) {
            jObjError.getString(key)
        } else {
            "Please Try Again"
        }
        errorLiveData.postValue(message)
    }

    fun createUser(name: List<String>, username: String) {
        runIO {
            when (val response = repo.createUser(name, username, "$dialCode-$mobile", email!!, uniqueId!!)) {
                is ResultWrapper.GenericError -> setError(response.error)
                is ResultWrapper.Success -> {
                    if (response.value.isSuccessful)
                        response.value.body()?.let {
                            repo.saveKeys(it)
                            isLoggedIn.postValue(true)
                            repo.verifyMobileUsingClaim(uniqueId!!)
                        }
                    else {
                        if (response.value.code() != 404)
                            response.value.errorBody()?.let { parseError(it, "description") }
                    }
                }
            }
        }
    }
}

enum class AccountStates {
    DO_NOT_EXIST,
    NUMBER_NOT_VERIFIED,
    EMAIL_NOT_VERIFIED,
    EXITS
}
