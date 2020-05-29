package com.codingblocks.cbonlineapp.auth

import com.codingblocks.cbonlineapp.util.PreferenceHelper
import com.codingblocks.onlineapi.Clients
import com.codingblocks.onlineapi.safeApiCall
import com.google.gson.JsonObject

class AuthRepository(
    val prefs: PreferenceHelper
) {

    suspend fun getToken(grantCode: String) = safeApiCall { Clients.api.getToken(grantCode) }

    suspend fun sendOtp(dialCode: String, mobile: String) = safeApiCall { Clients.api.getOtp(hashMapOf("mobile" to mobile, "dialCode" to dialCode)) }

    suspend fun verifyOtp(otp: Int, uniqueId: String) = safeApiCall { Clients.api.verifyOtp(uniqueId, hashMapOf("otp" to otp, "client" to "android")) }

    suspend fun findUser(userMap: HashMap<String, String>) = safeApiCall { Clients.api.findUser(userMap) }

    fun saveKeys(it: JsonObject) {
        with(it["jwt"].asString) {
            Clients.authJwt = this
            prefs.SP_JWT_TOKEN_KEY = this
        }
        with(it["refresh_token"].asString) {
            Clients.refreshToken = this
            prefs.SP_JWT_REFRESH_TOKEN = this
        }
    }

    suspend fun loginWithEmail(email: String, password: String) = safeApiCall {
        Clients.api.getJwtWithEmail(hashMapOf(
            "username" to email,
            "password" to password,
            "client" to "android"
        ))
    }

    suspend fun loginWithClaim(uniqueId: String) = safeApiCall { Clients.api.getJwtWithClaim(uniqueId) }

    suspend fun verifyMobileUsingClaim(uniqueId: String) = safeApiCall { Clients.api.verifyMobile(hashMapOf("claimId" to uniqueId)) }

    suspend fun createUser(name: List<String>, username: String, mobile: String, email: String, uniqueId: String) = safeApiCall {
        Clients.api.createUser(
            hashMapOf("username" to username,
                "mobile" to mobile,
                "firstname" to name[0],
                "lastname" to name[1],
                "email" to email,
                "claimId" to uniqueId))
    }
}
