package com.codingblocks.cbonlineapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences

class PreferenceManager {

    companion object {
        lateinit var instance: PreferenceManager
        const val APPLICATION_PREFERENCE = "app-preference"
        const val AUTH_TOKEN = "auth-token"
    }

    private val context: Context = CBOnlineApp.mInstance.applicationContext
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        APPLICATION_PREFERENCE, Context.MODE_PRIVATE)

    @SuppressLint("ApplySharedPref")
    //Cannot use .apply(), it will take time to save the token. We need token ASAP
    fun putAuthToken(authToken: String) {
        sharedPreferences.edit().putString(AUTH_TOKEN, "Bearer $authToken").commit()
    }

    val authToken: String
        get() = sharedPreferences.getString(AUTH_TOKEN, "")

    /**
     * Clears all the data that has been saved in the preferences file.
     */
    fun clear() {
        sharedPreferences.edit().clear().apply()
    }

}
