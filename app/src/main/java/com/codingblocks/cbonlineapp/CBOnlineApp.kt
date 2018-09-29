package com.codingblocks.cbonlineapp

import android.app.Application
import com.codingblocks.cbonlineapp.Utils.Prefs
import io.github.inflationx.calligraphy3.CalligraphyConfig
import io.github.inflationx.calligraphy3.CalligraphyInterceptor
import io.github.inflationx.viewpump.ViewPump



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
        ViewPump.init(ViewPump.builder()
                .addInterceptor(CalligraphyInterceptor(
                        CalligraphyConfig.Builder()
                                .setDefaultFontPath("fonts/Cabin-Medium.ttf")
                                .setFontAttrId(R.attr.fontPath)
                                .build()))
                .build())
    }
}