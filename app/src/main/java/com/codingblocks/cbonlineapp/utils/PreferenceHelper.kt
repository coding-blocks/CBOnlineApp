package com.codingblocks.cbonlineapp.utils

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import androidx.fragment.app.Fragment

inline fun <A : Activity> A.getPrefs() = Prefs(this)
inline fun <F : Fragment> F.getPrefs() = context?.let { Prefs(it) }

class Prefs(context: Context) {
    companion object {
        val PREFS_FILENAME = "com.codingblocks.cbonlineapp.prefs"
        val ACCESS_TOKEN = "access_token"
        val JWT_TOKEN = "jwt_token"
        val REFRESH_TOKEN = "refresh_token"
        val USER_IMAGE = "user_image"
        val ONEAUTH_ID = "oneauth_id"
        val USER_ID = "user_id"
        val WIFI = "wifi"
        val WIFI_VALUE = false


    }

    val prefs: SharedPreferences = context.getSharedPreferences(PREFS_FILENAME, 0)

    var SP_ACCESS_TOKEN_KEY: String
        get() = prefs.getString(ACCESS_TOKEN, ACCESS_TOKEN)
        set(value) {
            prefs.edit().putString(ACCESS_TOKEN, value).commit()
        }

    var SP_JWT_TOKEN_KEY: String
        get() = prefs.getString(JWT_TOKEN, JWT_TOKEN)
        set(value) {
            prefs.edit().putString(JWT_TOKEN, value).commit()
        }

    var SP_JWT_REFRESH_TOKEN: String
        get() = prefs.getString(REFRESH_TOKEN, REFRESH_TOKEN)
        set(value) {
            prefs.edit().putString(REFRESH_TOKEN, value).commit()
        }
    var SP_USER_IMAGE: String
        get() = prefs.getString(USER_IMAGE, USER_IMAGE)
        set(value) {
            prefs.edit().putString(USER_IMAGE, value).commit()
        }
    var SP_ONEAUTH_ID: String
        get() = prefs.getString(ONEAUTH_ID, ONEAUTH_ID)
        set(value) {
            prefs.edit().putString(ONEAUTH_ID, value).commit()
        }

    var SP_USER_ID: String
        get() = prefs.getString(USER_ID, USER_ID)
        set(value) {
            prefs.edit().putString(USER_ID, value).commit()
        }
    var SP_WIFI: Boolean
        get() = prefs.getBoolean(WIFI,WIFI_VALUE)
        set(value) {
            prefs.edit().putBoolean(WIFI, value).commit()
        }
}