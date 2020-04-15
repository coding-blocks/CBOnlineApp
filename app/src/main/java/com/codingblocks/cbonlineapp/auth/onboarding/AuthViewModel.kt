package com.codingblocks.cbonlineapp.auth.onboarding

import androidx.lifecycle.MutableLiveData
import com.codingblocks.cbonlineapp.baseclasses.BaseCBViewModel
import com.codingblocks.cbonlineapp.dashboard.home.DashboardHomeRepository
import com.codingblocks.cbonlineapp.util.extensions.runIO
import com.codingblocks.onlineapi.Clients
import com.codingblocks.onlineapi.ResultWrapper

class AuthViewModel(
    private val homeRepo: DashboardHomeRepository,
    private val repo: AuthRepository
) : BaseCBViewModel() {
    var claimId: String = ""
    var mobile: String = ""

    fun fetchToken(grantCode: String): MutableLiveData<Boolean> {
        val authComplete = MutableLiveData<Boolean>()
        runIO {
            when (val response = homeRepo.getToken(grantCode)) {
                is ResultWrapper.GenericError -> setError(response.error)
                is ResultWrapper.Success -> {
                    if (response.value.isSuccessful)
                        response.value.body()?.let {
                            val jwt = it.asJsonObject.get("jwt").asString
                            val rt = it.asJsonObject.get("refresh_token").asString
                            homeRepo.prefs.SP_JWT_TOKEN_KEY = jwt
                            homeRepo.prefs.SP_JWT_REFRESH_TOKEN = rt
                            Clients.authJwt = jwt
                            Clients.refreshToken = rt
                            authComplete.postValue(true)
                        }
                }
            }
        }
        return authComplete
    }

    fun getOtp(number: String, function: (id: String) -> Unit) {
        runIO {
            when (val response = repo.getOtp(number)) {
                is ResultWrapper.GenericError -> setError(response.error)
                is ResultWrapper.Success -> {
                    if (response.value.isSuccessful)
                        response.value.body()?.let {
                            val id = it["id"].asString
                            mobile = number
                            claimId = id
                            function(id)
                        }
                }
            }
        }
    }


    fun verifyOtp(otp:String,function: (id: String) -> Unit) {
        runIO {
            when (val response = repo.verifyOtp(claimId)) {
                is ResultWrapper.GenericError -> setError(response.error)
                is ResultWrapper.Success -> {
                    if (response.value.isSuccessful)
                        response.value.body()?.let {
                            val id = it["id"].asString
                        }
                }
            }
        }
    }
}
