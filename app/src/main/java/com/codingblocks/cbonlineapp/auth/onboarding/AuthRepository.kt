package com.codingblocks.cbonlineapp.auth.onboarding

import com.codingblocks.onlineapi.Clients
import com.codingblocks.onlineapi.safeApiCall

class AuthRepository {

    suspend fun getOtp(mobile: String) = safeApiCall { Clients.api.getOtpV2(hashMapOf("mobile" to mobile)) }

}
