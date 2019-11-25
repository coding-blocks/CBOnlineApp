package com.codingblocks.cbonlineapp.home

import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.codingblocks.cbonlineapp.util.JWTUtils
import com.codingblocks.cbonlineapp.util.PreferenceHelper
import com.codingblocks.cbonlineapp.util.extensions.retrofitCallback
import com.codingblocks.onlineapi.Clients
import com.codingblocks.onlineapi.models.Player
import com.onesignal.OneSignal

class HomeActivityViewModel : ViewModel() {
    internal var doubleBackToExitPressedOnce = false
    var mFragmentToSet: Fragment? = null
    lateinit var prefs: PreferenceHelper
    var invalidateTokenProgress: MutableLiveData<Boolean> = MutableLiveData()
    var fetchTokenProgress: MutableLiveData<Boolean> = MutableLiveData()
    var getMeProgress: MutableLiveData<Boolean> = MutableLiveData()
    var clearData: MutableLiveData<Boolean> = MutableLiveData(false)
    var isAdmin: MutableLiveData<Boolean> = MutableLiveData(false)


    fun invalidateToken() {
        prefs.SP_ACCESS_TOKEN_KEY = PreferenceHelper.ACCESS_TOKEN
        Clients.api.logout().enqueue(retrofitCallback { _, response ->
            response?.let {
                invalidateTokenProgress.value = it.isSuccessful
            }
        })
    }

    fun fetchToken(grantCode: String) {
        Clients.api.getToken(grantCode).enqueue(retrofitCallback { _, response ->
            response.let {
                if (response?.isSuccessful == true) {
                    val jwt = response.body()?.asJsonObject?.get("jwt")?.asString!!
                    val rt = response.body()?.asJsonObject?.get("refresh_token")?.asString!!
                    prefs.SP_ACCESS_TOKEN_KEY = grantCode
                    prefs.SP_JWT_TOKEN_KEY = jwt
                    prefs.SP_JWT_REFRESH_TOKEN = rt
                    Clients.authJwt = jwt
                    Clients.refreshToken = rt

                    val status = OneSignal.getPermissionSubscriptionState()
                    // Set Player ID For OneSignal
                    Clients.onlineV2JsonApi.setPlayerId(Player(playerId = status.subscriptionStatus.userId))
                        .enqueue(retrofitCallback { _, _ ->
                        })
                    fetchTokenProgress.value = true
                } else if (response?.code() == 500 && prefs.SP_ACCESS_TOKEN_KEY == "access_token") {
                    fetchTokenProgress.value = false
                }
            }
        })
    }

    fun getMe() {
        setJWTToken()
        Clients.onlineV2JsonApi.getMe().enqueue(retrofitCallback { _, resp ->
            resp.let {
                if (resp?.isSuccessful == true) {
                    resp.body()?.let {
                        if (prefs.SP_ONEAUTH_ID == PreferenceHelper.ONEAUTH_ID) {
                            clearData.postValue(true)
                        }
                        prefs.SP_USER_ID = it.id
                        prefs.SP_ONEAUTH_ID = it.oneauthId
                        prefs.SP_USER_IMAGE = it.photo ?: "Empty"
                        prefs.SP_USER_NAME = it.firstname + " " + it.lastname
                        prefs.SP_ROLE_ID = it.roleId
                        if (it.roleId == 1 || it.roleId == 3)
                            isAdmin.postValue(true)

                    }
                    getMeProgress.value = true
                } else {
                    getMeProgress.value = false
                }
            }
        })
    }

    fun setJWTToken() {
        Clients.authJwt = prefs.SP_JWT_TOKEN_KEY
        Clients.refreshToken = prefs.SP_JWT_REFRESH_TOKEN
        //            Update User Token on Login
        if (JWTUtils.isExpired(prefs.SP_JWT_TOKEN_KEY))
            refreshToken()
    }

    private fun refreshToken() {
        Clients.api.refreshToken(prefs.SP_JWT_REFRESH_TOKEN)
            .enqueue(retrofitCallback { throwable, response ->
                response?.body().let {
                    if (response?.isSuccessful == true) {
                        val jwt = it?.asJsonObject?.get("jwt")?.asString!!
                        val rt = it.asJsonObject?.get("refresh_token")?.asString!!
                        prefs.SP_JWT_TOKEN_KEY = jwt
                        prefs.SP_JWT_REFRESH_TOKEN = rt
                        Clients.authJwt = jwt
                    }
                }
            })
    }
}
