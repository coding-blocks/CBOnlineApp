package com.codingblocks.cbonlineapp.auth

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.liveData
import com.codingblocks.cbonlineapp.baseclasses.BaseCBViewModel
import com.codingblocks.cbonlineapp.util.extensions.runIO
import com.codingblocks.cbonlineapp.util.savedStateValue
import com.codingblocks.onlineapi.Clients
import com.codingblocks.onlineapi.ResultWrapper
import kotlinx.coroutines.Dispatchers

const val PHONE_NUMBER = "phoneNumber"

class AuthViewModel(
    handle: SavedStateHandle,
    private val repo: AuthRepository
) : BaseCBViewModel() {

    var mobile by savedStateValue<String>(handle, PHONE_NUMBER)

    fun fetchToken(grantCode: String) = liveData<Boolean>(Dispatchers.IO) {
        when (val response = repo.getToken(grantCode)) {
            is ResultWrapper.GenericError -> setError(response.error)
            is ResultWrapper.Success -> {
                if (response.value.isSuccessful)
                    response.value.body()?.let {
                        val jwt = it.asJsonObject.get("jwt").asString
                        val rt = it.asJsonObject.get("refresh_token").asString
                        repo.prefs.SP_JWT_TOKEN_KEY = jwt
                        repo.prefs.SP_JWT_REFRESH_TOKEN = rt
                        Clients.authJwt = jwt
                        Clients.refreshToken = rt
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
                        }
                }
            }
        }
    }
}
