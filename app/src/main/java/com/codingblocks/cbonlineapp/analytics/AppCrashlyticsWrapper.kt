package com.codingblocks.cbonlineapp.analytics

import android.util.Log
import com.codingblocks.cbonlineapp.BuildConfig
import com.google.firebase.crashlytics.FirebaseCrashlytics

/**
 * Created by championswimmer on 2020-01-26.
 */
object AppCrashlyticsWrapper {
    fun log(msg: String) {
        if (BuildConfig.DEBUG) {
            Log.w("Crashlytics", msg)
        } else {
            FirebaseCrashlytics.getInstance().log(msg)
        }
    }
}
