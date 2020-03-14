package com.codingblocks.cbonlineapp.profile

import androidx.lifecycle.MutableLiveData
import com.codingblocks.cbonlineapp.baseclasses.BaseCBViewModel
import com.codingblocks.cbonlineapp.util.extensions.runIO
import com.codingblocks.onlineapi.Clients
import com.codingblocks.onlineapi.ResultWrapper
import com.codingblocks.onlineapi.fetchError
import com.codingblocks.onlineapi.models.User
import org.json.JSONObject

/**
 * @author aggarwalpulkit596
 */
class ProfileViewModel(private val repo: ProfileRepository) : BaseCBViewModel() {

    fun fetchUser(): MutableLiveData<User> {
        val user = MutableLiveData<User>()
        runIO {
            when (val response = repo.fetchUser()) {
                is ResultWrapper.GenericError -> setError(response.error)
                is ResultWrapper.Success -> {
                    if (response.value.isSuccessful)
                        response.value.body()?.let {
                            user.postValue(it)
                        }
                    else {
                        setError(fetchError(response.value.code()))
                    }
                }
            }
        }
        return user
    }

    fun updateUser(id: String, map: Map<String, String>): MutableLiveData<String> {
        val res = MutableLiveData<String>()
        runIO {
            when (val response = repo.updateUser(id, map)) {
                is ResultWrapper.GenericError -> res.postValue(response.error)
                is ResultWrapper.Success -> {
                    if (response.value.isSuccessful)
                        response.value.body()?.let {
                            refreshToken()
                            res.postValue("Updated Successfully")
                        }
                    else {
                        val errRes = response.value.errorBody()?.string()
                        val error =
                            if (errRes.isNullOrEmpty()) "Please Try Again" else JSONObject(
                                errRes
                            ).getString("description")
                        res.postValue(error.capitalize())
                    }
                }
            }
        }
        return res
    }

    fun refreshToken() {
        runIO {
            when (val response = repo.refreshToken()) {
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
                        }
                }
            }
        }
    }
}
