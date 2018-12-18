package com.codingblocks.cbonlineapp

import android.app.Application
import android.content.Context
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
        lateinit var APP_CONTEXT: Context
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

        APP_CONTEXT = this
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1)
//            shortcutAction(::updateShortcuts)
    }
}