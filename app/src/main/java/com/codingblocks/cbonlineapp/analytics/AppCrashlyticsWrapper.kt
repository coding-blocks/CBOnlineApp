package com.codingblocks.cbonlineapp.analytics

import android.util.Log
import com.codingblocks.cbonlineapp.BuildConfig
import com.crashlytics.android.core.CrashlyticsCore

/**
 * Created by championswimmer on 2020-01-26.
 */
object AppCrashlyticsWrapper {
    fun log(msg: String) {
        if (BuildConfig.DEBUG) {
            Log.w("Crashlytics", msg)
        } else {
            CrashlyticsCore.getInstance().log(msg)
        }
    }
}
