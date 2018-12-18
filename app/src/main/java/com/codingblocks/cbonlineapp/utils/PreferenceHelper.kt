package com.codingblocks.cbonlineapp.Utils

import android.content.Context
import android.content.SharedPreferences

class Prefs(context: Context) {
    val PREFS_FILENAME = "com.codingblocks.cbonlineapp.prefs"
    val ACCESS_TOKEN = "access_token"
    val JWT_TOKEN = "jwt_token"
    val REFRESH_TOKEN = "refresh_token"
    val USER_IMAGE = "user_image"

    val prefs: SharedPreferences = context.getSharedPreferences(PREFS_FILENAME, 0);

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
}