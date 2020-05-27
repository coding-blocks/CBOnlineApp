package com.codingblocks.cbonlineapp.auth

import com.codingblocks.cbonlineapp.util.PreferenceHelper
import com.codingblocks.onlineapi.Clients
import com.codingblocks.onlineapi.safeApiCall

class AuthRepository(
    val prefs: PreferenceHelper
) {

    suspend fun getToken(grantCode: String) = safeApiCall { Clients.api.getToken(grantCode) }

    suspend fun sendOtp(dialCode: String, mobile: String) = safeApiCall { Clients.api.getOtp(hashMapOf("mobile" to mobile, "dialCode" to dialCode)) }
}
