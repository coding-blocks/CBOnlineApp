package com.codingblocks.cbonlineapp.Utils

import android.content.Context
import android.content.SharedPreferences

class Prefs(context: Context) {
    val PREFS_FILENAME = "com.codingblocks.cbonlineapp.prefs"
    val ACCESS_TOKEN = "access_token"
    val prefs: SharedPreferences = context.getSharedPreferences(PREFS_FILENAME, 0);

    var SP_ACCESS_TOKEN_KEY: String
        get() = prefs.getString(ACCESS_TOKEN,ACCESS_TOKEN)
        set(value) = prefs.edit().putString(ACCESS_TOKEN, value).apply()
}