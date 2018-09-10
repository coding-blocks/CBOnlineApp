package com.codingblocks.cbonlineapp

import android.app.Application
import com.codingblocks.cbonlineapp.Utils.Prefs

val prefs: Prefs by lazy {
    CBOnlineApp.prefs!!
}

class CBOnlineApp : Application() {
    companion object {
        var prefs: Prefs? = null
    }

    override fun onCreate() {
        prefs = Prefs(applicationContext)
        super.onCreate()
    }
}