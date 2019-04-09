package com.codingblocks.cbonlineapp

import android.app.Application
import android.content.Context
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.core.CrashlyticsCore
import com.squareup.picasso.Picasso
import io.fabric.sdk.android.Fabric
import io.github.inflationx.calligraphy3.CalligraphyConfig
import io.github.inflationx.calligraphy3.CalligraphyInterceptor
import io.github.inflationx.viewpump.ViewPump

class CBOnlineApp : Application() {
    override fun onCreate() {
        super.onCreate()
        mInstance = this
        Picasso.setSingletonInstance(Picasso.Builder(this).build())
        ViewPump.init(
            ViewPump.builder()
                .addInterceptor(
                    CalligraphyInterceptor(
                        CalligraphyConfig.Builder()
                            .setDefaultFontPath("fonts/NunitoSans-Regular.ttf")
                            .setFontAttrId(R.attr.fontPath)
                            .build()
                    )
                )
                .build()
        )
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1)
//            shortcutAction(::updateShortcuts)


        val crashlyticsKit = Crashlytics.Builder()
            .core(CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build())
            .build()
        Fabric.with(this, crashlyticsKit)
    }

    companion object {
        lateinit var mInstance: CBOnlineApp
        fun getContext(): Context? {
            return mInstance.applicationContext
        }
    }

}
