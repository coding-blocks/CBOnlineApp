package com.codingblocks.cbonlineapp.util

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences

class PreferenceHelper private constructor() {
    companion object {
        const val PREFS_FILENAME = "com.codingblocks.cbonlineapp.prefs"
        const val ACCESS_TOKEN = "access_token"
        const val JWT_TOKEN = "jwt_token"
        const val REFRESH_TOKEN = "refresh_token"
        const val USER_IMAGE = "user_image"
        const val ONEAUTH_ID = "oneauth_id"
        const val USER_ID = "user_id"
        const val USER_NAME = "user_name"
        const val WIFI = "wifi"
        const val WIFI_DEFAULT = false
        const val DATA_LIMIT = "data_limit"
        const val DATA_LIMIT_DEFAULT = 1.0f
        private var prefs: SharedPreferences? = null
        private var instance: PreferenceHelper = PreferenceHelper()

        fun getPrefs(context: Context): PreferenceHelper {
            if (prefs == null) {
                prefs = context.getSharedPreferences(PREFS_FILENAME, MODE_PRIVATE)
            }
            return instance
        }
    }

    var SP_ACCESS_TOKEN_KEY: String
        get() = prefs?.getString(ACCESS_TOKEN, ACCESS_TOKEN) ?: ""
        set(value) {
            prefs?.edit()?.putString(ACCESS_TOKEN, value)?.apply()
        }

    var SP_JWT_TOKEN_KEY: String
        get() = prefs?.getString(JWT_TOKEN, JWT_TOKEN) ?: ""
        set(value) {
            prefs?.edit()?.putString(JWT_TOKEN, value)?.apply()
        }

    var SP_JWT_REFRESH_TOKEN: String
        get() = prefs?.getString(REFRESH_TOKEN, REFRESH_TOKEN) ?: ""
        set(value) {
            prefs?.edit()?.putString(REFRESH_TOKEN, value)?.apply()
        }
    var SP_USER_IMAGE: String
        get() = prefs?.getString(USER_IMAGE, USER_IMAGE) ?: ""
        set(value) {
            prefs?.edit()?.putString(USER_IMAGE, value)?.apply()
        }
    var SP_ONEAUTH_ID: String
        get() = prefs?.getString(ONEAUTH_ID, ONEAUTH_ID) ?: ""
        set(value) {
            prefs?.edit()?.putString(ONEAUTH_ID, value)?.apply()
        }

    var SP_USER_ID: String
        get() = prefs?.getString(USER_ID, USER_ID) ?: ""
        set(value) {
            prefs?.edit()?.putString(USER_ID, value)?.apply()
        }

    var SP_USER_NAME: String
        get() = prefs?.getString(USER_NAME, USER_NAME) ?: ""
        set(value) {
            prefs?.edit()?.putString(USER_NAME, value)?.apply()
        }

    var SP_WIFI: Boolean
        get() = prefs?.getBoolean(WIFI, WIFI_DEFAULT) ?: WIFI_DEFAULT
        set(value) {
            prefs?.edit()?.putBoolean(WIFI, value)?.apply()
        }
    var SP_DATA_LIMIT: Float
        get() = prefs?.getFloat(DATA_LIMIT, DATA_LIMIT_DEFAULT) ?: DATA_LIMIT_DEFAULT
        set(value) {
            prefs?.edit()?.putFloat(DATA_LIMIT, value)?.apply()
        }
}
