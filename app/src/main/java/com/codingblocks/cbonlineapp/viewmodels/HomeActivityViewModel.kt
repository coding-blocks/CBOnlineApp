package com.codingblocks.cbonlineapp.viewmodels

import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.codingblocks.cbonlineapp.database.NotificationDao
import com.codingblocks.cbonlineapp.extensions.retrofitCallback
import com.codingblocks.cbonlineapp.util.PreferenceHelper
import com.codingblocks.onlineapi.Clients
import com.codingblocks.onlineapi.models.Player
import com.onesignal.OneSignal

class HomeActivityViewModel(
    private val notificationDao: NotificationDao
) : ViewModel() {
    internal var doubleBackToExitPressedOnce = false
    var mFragmentToSet: Fragment? = null

    lateinit var prefs: PreferenceHelper

    fun getNotificationCount() = notificationDao.count

    var invalidateTokenProgress: MutableLiveData<Boolean> = MutableLiveData()
    var fetchTokenProgress: MutableLiveData<Boolean> = MutableLiveData()
    var getMeProgress: MutableLiveData<Boolean> = MutableLiveData()

    fun invalidateToken() {
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
        Clients.api.getMe().enqueue(retrofitCallback { _, resp ->
            resp.let {
                if (resp?.isSuccessful == true) {
                    resp.body()?.let {
                        try {
                            val jSONObject =
                                it.getAsJsonObject("data").getAsJsonObject("attributes")
                            prefs.SP_USER_ID = it.getAsJsonObject("data").get("id").asString
                            prefs.SP_ONEAUTH_ID = jSONObject.get("oneauth-id").asString
                            prefs.SP_USER_IMAGE = jSONObject.get("photo").asString
                            prefs.SP_USER_NAME = jSONObject.get("firstname").asString + " " + jSONObject.get("lastname").asString
                        } catch (e: Exception) {
                        }
                    }
                    getMeProgress.value = true
                } else {
                    getMeProgress.value = false
                }
            }
        })
    }

    private fun setJWTToken() {
        Clients.authJwt = prefs.SP_JWT_TOKEN_KEY
    }
}
