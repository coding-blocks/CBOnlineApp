package com.codingblocks.cbonlineapp.viewmodels

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import com.codingblocks.cbonlineapp.database.NotificationDao
import com.codingblocks.cbonlineapp.extensions.retrofitCallback
import com.codingblocks.cbonlineapp.util.PreferenceHelper
import com.codingblocks.onlineapi.Clients
import com.codingblocks.onlineapi.models.Player
import com.onesignal.OneSignal

class HomeActivityViewModel(var notificationDao: NotificationDao) : ViewModel() {
    internal var doubleBackToExitPressedOnce = false
    var mFragmentToSet: Fragment? = null

    lateinit var prefs: PreferenceHelper

    fun invalidateToken(onCompleteListener: OnCompleteListener) {
        Clients.api.logout().enqueue(retrofitCallback { throwable, response ->
            response?.let {
                if (it.isSuccessful) {
                    onCompleteListener.onResponseComplete()
                }
            }
        })
    }

    fun fetchToken(grantCode: String, onCompleteListener: OnCompleteListener) {
        Clients.api.getToken(grantCode).enqueue(retrofitCallback { error, response ->
            response.let {
                if (response?.isSuccessful == true) {
                    val jwt = response.body()?.asJsonObject?.get("jwt")?.asString!!
                    val rt = response.body()?.asJsonObject?.get("refresh_token")?.asString!!
                    prefs.SP_ACCESS_TOKEN_KEY = grantCode
                    prefs.SP_JWT_TOKEN_KEY = jwt
                    prefs.SP_JWT_REFRESH_TOKEN = rt
                    Clients.authJwt = jwt
                    val status = OneSignal.getPermissionSubscriptionState()
                    //Set Player ID For OneSignal
                    Clients.onlineV2JsonApi.setPlayerId(Player(playerId = status.subscriptionStatus.userId))
                        .enqueue(retrofitCallback { _, _ ->

                        })
                    onCompleteListener.onResponseComplete()
                } else if (response?.code() == 500 && prefs.SP_ACCESS_TOKEN_KEY == "access_token") {
                    onCompleteListener.onResponseFailed()
                }
            }
        })
    }

    fun getMe(onCompleteListener: OnCompleteListener) {
        setJWTToken()
        Clients.api.getMe().enqueue(retrofitCallback { t, resp ->
            resp.let {
                if(resp?.isSuccessful == true){
                    resp.body()?.let {
                        try {
                            val jSONObject =
                                it.getAsJsonObject("data").getAsJsonObject("attributes")
                            prefs.SP_USER_ID = it.getAsJsonObject("data").get("id").asString
                            prefs.SP_ONEAUTH_ID = jSONObject.get("oneauth-id").asString
                            prefs.SP_USER_IMAGE = jSONObject.get("photo").asString
                        } catch (e: Exception) {
                        }
                    }
                    onCompleteListener.onResponseComplete()
                }else{
                    onCompleteListener.onResponseFailed()
                }
            }
        })
    }

    private fun setJWTToken() {
        Clients.authJwt = prefs.SP_JWT_TOKEN_KEY
    }
}

interface OnCompleteListener {
    fun onResponseComplete()
    fun onResponseFailed()
}
