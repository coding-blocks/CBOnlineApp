package com.codingblocks.cbonlineapp.auth

import com.codingblocks.cbonlineapp.util.PreferenceHelper
import com.codingblocks.onlineapi.CBOnlineLib
import com.codingblocks.onlineapi.safeApiCall
import com.google.gson.JsonObject

class AuthRepository(
    val prefs: PreferenceHelper
) {

    suspend fun getToken(grantCode: String) = safeApiCall { CBOnlineLib.api.getToken(grantCode) }

    suspend fun sendOtp(dialCode: String, mobile: String) = safeApiCall {
        CBOnlineLib.api.getOtp(hashMapOf("mobile" to mobile, "dialCode" to dialCode))
    }

    suspend fun verifyOtp(otp: Int, uniqueId: String) =
        safeApiCall { CBOnlineLib.api.verifyOtp(uniqueId, hashMapOf("otp" to otp, "client" to "android")) }

    suspend fun findUser(userMap: HashMap<String, String>) = safeApiCall { CBOnlineLib.api.findUser(userMap) }

    fun saveKeys(it: JsonObject) {
        with(it["jwt"].asString) {
            prefs.SP_JWT_TOKEN_KEY = this
        }
        with(it["refresh_token"].asString) {
            prefs.SP_JWT_REFRESH_TOKEN = this
        }
    }

    suspend fun loginWithEmail(email: String, password: String) = safeApiCall {
        CBOnlineLib.api.getJwtWithEmail(
            hashMapOf(
                "username" to email,
                "password" to password,
                "client" to "android"
            )
        )
    }

    suspend fun loginWithClaim(uniqueId: String) = safeApiCall { CBOnlineLib.api.getJwtWithClaim(uniqueId) }

    suspend fun verifyMobileUsingClaim(uniqueId: String) =
        safeApiCall { CBOnlineLib.api.verifyMobile(hashMapOf("claimId" to uniqueId)) }

    suspend fun createUser(name: List<String>, username: String, mobile: String, email: String, uniqueId: String) =
        safeApiCall {
            CBOnlineLib.api.createUser(
                hashMapOf(
                    "username" to username,
                    "mobile" to mobile,
                    "firstname" to name[0],
                    "lastname" to name[1],
                    "email" to email,
                    "claimId" to uniqueId
                )
            )
        }
}
